package escuela.cobranza.service;
import escuela.cobranza.entity.*;
import escuela.cobranza.repository.*;
import escuela.finanzas.entity.*;
import escuela.inscripcion.entity.*;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoTutorRepository;
import escuela.institucion.entity.*;
import escuela.tutor.entity.Tutor;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.auditoria.service.RegistroAuditoriaService;
import java.math.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;
class AnticipoLifecycleTest {
 final AcuerdoAnticipadoRepository acuerdos=mock(AcuerdoAnticipadoRepository.class);
 final CargoRepository cargos=mock(CargoRepository.class);
 final AjusteCargoRepository ajustes=mock(AjusteCargoRepository.class);
 final AlumnoTutorRepository vinculos=mock(AlumnoTutorRepository.class);
 final AlcanceDatosService alcance=mock(AlcanceDatosService.class);
 final AnticipoLifecycleService s=new AnticipoLifecycleService(acuerdos,cargos,ajustes,vinculos,alcance,mock(RegistroAuditoriaService.class));
 AcuerdoAnticipado a;Pago p;Usuario actor;List<SolicitudAplicacionPago> solicitudes;
 BigDecimal n(String x){return new BigDecimal(x);}
 @BeforeEach void setup(){var inst=new Institucion();inst.setId(1L);inst.setZonaHoraria("America/Mexico_City");var plantel=new Plantel();plantel.setId(2L);var tutor=new Tutor();tutor.setId(3L);actor=new Usuario();actor.setId(4L);a=new AcuerdoAnticipado();a.setId(5L);a.setInstitucion(inst);a.setPlantel(plantel);a.setTutor(tutor);a.setFolio("ANT-TEST");a.setMoneda("MXN");a.setFechaLimite(LocalDate.now().plusDays(2));a.setPoliticaBeca("CONSERVAR");a.setTotalBeneficio(n("160.00"));a.setTotalPagar(n("1440.00"));a.setMotivo("Pago completo");p=new Pago();p.setId(6L);p.setFolio("PAG-TEST");p.setInstitucion(inst);p.setPlantelRegistro(plantel);p.setTutor(tutor);p.setMoneda("MXN");p.setMonto(n("1440.00"));p.setFechaPago(Instant.now());p.setAcuerdoAnticipadoId(5L);solicitudes=new ArrayList<>();
  for(long id:List.of(10L,11L)){var alumno=new Alumno();alumno.setId(id);alumno.setInstitucion(inst);alumno.setActivo(true);var i=new Inscripcion();i.setAlumno(alumno);i.setPlantel(plantel);i.setEstado(EstadoInscripcion.ACTIVA);var concepto=new ConceptoCobro();concepto.setCategoria(CategoriaConceptoCobro.COLEGIATURA);concepto.setPermiteDescuento(true);var c=new Cargo();c.setId(id);c.setInscripcion(i);c.setConceptoCobro(concepto);c.setMoneda("MXN");c.setImporteOriginal(n("1000.00"));c.setPeriodoCobroInicio(LocalDate.now().withDayOfMonth(1));c.setPeriodoCobroFin(LocalDate.now().withDayOfMonth(20));var b=new AjusteCargo();b.setTipo(TipoAjusteCargo.BECA);b.setEfecto(EfectoAjusteCargo.DISMINUCION);b.setMonto(n("200.00"));c.getAjustes().add(b);var d=new AcuerdoAnticipadoCargo();d.setCargo(c);d.setAcuerdo(a);d.setOriginal(n("1000.00"));d.setTotalActual(n("800.00"));d.setBeca(n("200.00"));d.setBase(n("800.00"));d.setBeneficio(n("80.00"));d.setPagar(n("720.00"));a.getCargos().add(d);var sp=new SolicitudAplicacionPago();sp.setCargo(c);sp.setMontoSolicitado(n("720.00"));solicitudes.add(sp);when(cargos.findByIdForUpdate(id)).thenReturn(Optional.of(c));}
  when(acuerdos.bloquear(5L)).thenReturn(Optional.of(a));when(vinculos.tieneResponsabilidadFinancieraVigente(anyLong(),eq(3L),any())).thenReturn(true);when(ajustes.save(any())).thenAnswer(x->x.getArgument(0));
 }
 @Test void aplicarSoloTrasPagoCompletoConservaBecaYReduceCadaMensualidad(){s.aplicar(p,actor,solicitudes);assertThat(a.getEstado()).isEqualTo("APLICADO");for(var d:a.getCargos()){assertThat(escuela.cobranza.support.CalculoCargo.total(d.getCargo())).isEqualByComparingTo("720");assertThat(d.getCargo().getAjustes()).hasSize(2);}verify(ajustes,times(2)).save(any());}
 @Test void incompletoFueraDeFechaODistribucionInvalidaNoObtienenBeneficio(){p.setMonto(n("1400"));assertThatThrownBy(()->s.aplicar(p,actor,solicitudes)).hasMessageContaining("completo");p.setMonto(n("1440"));var fecha=p.getFechaPago().atZone(ZoneId.of("America/Mexico_City")).toLocalDate();a.setFechaLimite(fecha.minusDays(1));assertThatThrownBy(()->s.aplicar(p,actor,solicitudes)).hasMessageContaining("fecha límite");a.setFechaLimite(fecha.plusDays(1));solicitudes.removeLast();assertThatThrownBy(()->s.aplicar(p,actor,solicitudes)).hasMessageContaining("distribución");verify(ajustes,never()).save(any());}
 @Test void recibidoMayorCubreAcuerdoSinAumentarBeneficioNiDistribucion(){p.setMonto(n("1500"));s.aplicar(p,actor,solicitudes);assertThat(a.getTotalPagar()).isEqualByComparingTo("1440");assertThat(a.getTotalBeneficio()).isEqualByComparingTo("160");assertThat(solicitudes.stream().map(SolicitudAplicacionPago::getMontoSolicitado).reduce(BigDecimal.ZERO,BigDecimal::add)).isEqualByComparingTo("1440");assertThat(p.getMonto()).isEqualByComparingTo("1500");}
 @Test void snapshotsProtegenCambiosYPropietarios(){a.getCargos().getFirst().getCargo().setImporteOriginal(n("1100"));assertThatThrownBy(()->s.aplicar(p,actor,solicitudes)).hasMessageContaining("cambiaron");var otro=new Tutor();otro.setId(999L);p.setTutor(otro);assertThatThrownBy(()->s.aplicar(p,actor,solicitudes)).hasMessageContaining("no pertenece");verify(ajustes,never()).save(any());}
 @Test void sustitucionSoloOffsetDeBecaEnCargosSeleccionados(){a.setPoliticaBeca("SUSTITUIR");a.setTotalPagar(n("1840"));p.setMonto(n("1840"));for(var d:a.getCargos()){d.setPagar(n("920"));}solicitudes.forEach(x->x.setMontoSolicitado(n("920")));s.aplicar(p,actor,solicitudes);for(var d:a.getCargos()){assertThat(escuela.cobranza.support.CalculoCargo.total(d.getCargo())).isEqualByComparingTo("920");assertThat(AnticipoLifecycleService.beca(d.getCargo())).isEqualByComparingTo("200");}verify(ajustes,times(4)).save(any());}
 @Test void deshacerRestauraAjustesSinBorrarBecaYLiberaAsociaciones(){s.aplicar(p,actor,solicitudes);var originales=a.getCargos().stream().flatMap(d->d.getCargo().getAjustes().stream()).filter(j->j.getAcuerdoAnticipadoId()!=null).toList();when(ajustes.findAllByAcuerdoAnticipadoIdAndReversaDeIsNullOrderByIdAsc(5L)).thenReturn(originales);s.deshacer(p,actor,"Pago cancelado");assertThat(a.getEstado()).isEqualTo("CANCELADO");for(var d:a.getCargos()){assertThat(d.isActivo()).isFalse();assertThat(escuela.cobranza.support.CalculoCargo.total(d.getCargo())).isEqualByComparingTo("800");}s.deshacer(p,actor,"Otro intento");verify(ajustes,times(4)).save(any());}
 @Test void sinAcuerdoNoCambiaFlujoNormal(){p.setAcuerdoAnticipadoId(null);s.aplicar(p,actor,solicitudes);s.deshacer(p,actor,"Normal");verifyNoInteractions(acuerdos,ajustes);}
}
