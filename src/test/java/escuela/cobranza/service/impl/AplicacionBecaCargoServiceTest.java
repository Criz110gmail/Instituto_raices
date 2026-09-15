package escuela.cobranza.service.impl;

import escuela.cobranza.entity.*;import escuela.cobranza.repository.*;import escuela.inscripcion.entity.Inscripcion;import escuela.seguridad.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;import org.mockito.ArgumentCaptor;import java.math.BigDecimal;import java.time.LocalDate;import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;import static org.mockito.Mockito.*;

class AplicacionBecaCargoServiceTest {
 private final BecaAlumnoRepository becas=mock(BecaAlumnoRepository.class);private final AjusteCargoRepository ajustes=mock(AjusteCargoRepository.class);
 private final AplicacionBecaCargoService service=new AplicacionBecaCargoService(becas,ajustes,mock(UsuarioRepository.class));
 @Test void congelaPorcentajeConRedondeoHalfUp(){Cargo c=cargo("999.99");BecaAlumno b=beca(ModalidadBeca.PORCENTAJE,"12.5",null);when(becas.buscarAplicable(any(),any(),any(),any())).thenReturn(List.of(b));service.aplicar(c);ArgumentCaptor<AjusteCargo> cap=ArgumentCaptor.forClass(AjusteCargo.class);verify(ajustes).saveAndFlush(cap.capture());assertThat(cap.getValue().getMonto()).isEqualByComparingTo("125.00");assertThat(cap.getValue().getBaseCalculo()).isEqualByComparingTo("999.99");assertThat(cap.getValue().getPorcentajeAplicado()).isEqualByComparingTo("12.5");}
 @Test void limitaMontoFijoAlImporteOriginal(){Cargo c=cargo("500.00");when(becas.buscarAplicable(any(),any(),any(),any())).thenReturn(List.of(beca(ModalidadBeca.MONTO_FIJO,null,"900.00")));service.aplicar(c);ArgumentCaptor<AjusteCargo> cap=ArgumentCaptor.forClass(AjusteCargo.class);verify(ajustes).saveAndFlush(cap.capture());assertThat(cap.getValue().getMonto()).isEqualByComparingTo("500.00");}
 @Test void reintentoNoDuplicaAjuste(){Cargo c=cargo("500.00");BecaAlumno b=beca(ModalidadBeca.PORCENTAJE,"10",null);when(becas.buscarAplicable(any(),any(),any(),any())).thenReturn(List.of(b));when(ajustes.existsByCargoIdAndBecaAlumnoIdAndReversaDeIsNull(3L,4L)).thenReturn(true);service.aplicar(c);verify(ajustes,never()).saveAndFlush(any());}
 private Cargo cargo(String importe){var i=new Inscripcion();i.setId(1L);var concepto=new ConceptoCobro();concepto.setId(2L);Cargo c=new Cargo();c.setId(3L);c.setInscripcion(i);c.setConceptoCobro(concepto);c.setImporteOriginal(new BigDecimal(importe));c.setPeriodoCobroInicio(LocalDate.of(2026,9,1));c.setPeriodoCobroFin(LocalDate.of(2026,9,30));c.setFechaEmision(LocalDate.of(2026,9,1));return c;}
 private BecaAlumno beca(ModalidadBeca modalidad,String porcentaje,String monto){var tipo=new TipoBeca();tipo.setNombre("Académica");BecaAlumno b=new BecaAlumno();b.setId(4L);b.setTipoBeca(tipo);b.setModalidad(modalidad);b.setPorcentaje(porcentaje==null?null:new BigDecimal(porcentaje));b.setMontoFijo(monto==null?null:new BigDecimal(monto));b.setMotivo("Aprovechamiento");return b;}
}
