package escuela.cobranza.service.impl;

import escuela.alumno.entity.Alumno;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.PoliticaRecargoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.dto.request.GeneracionRecargosRequest;
import escuela.inscripcion.entity.Inscripcion;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PoliticaRecargoServiceImplTest {

    private final PoliticaRecargoRepository politicas = mock(PoliticaRecargoRepository.class);
    private final ConceptoCobroRepository conceptos = mock(ConceptoCobroRepository.class);
    private final CargoRepository cargos = mock(CargoRepository.class);
    private final AjusteCargoRepository ajustes = mock(AjusteCargoRepository.class);
    private final PoliticaRecargoServiceImpl service = new PoliticaRecargoServiceImpl(
            politicas, conceptos, cargos, ajustes, new PoliticaRecargoMapper());

    @Test
    void generaPorcentajeSobreBaseSinDescuentosYDespuesDeLaGracia() {
        Cargo cargo = cargo(new BigDecimal("1000.00"), LocalDate.of(2026, 9, 20));
        AjusteCargo descuento = new AjusteCargo();
        descuento.setTipo(TipoAjusteCargo.DESCUENTO);
        descuento.setEfecto(EfectoAjusteCargo.DISMINUCION);
        descuento.setMonto(new BigDecimal("100.00"));
        cargo.getAjustes().add(descuento);
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.PORCENTAJE,
                PeriodicidadRecargo.UNICA, TipoLimiteRecargo.SIN_LIMITE);
        politica.setPorcentaje(new BigDecimal("10.0000"));
        politica.setDiasGracia(5);
        preparar(cargo, politica, BigDecimal.ZERO);
        when(ajustes.insertarRecargoSiAusente(anyLong(), any(), any(), any(), anyString(),
                nullable(Long.class), any(), anyLong(), anyString())).thenReturn(1);

        var antes = service.generar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 9, 25)));
        assertThat(antes.recargosGenerados()).isZero();

        var respuesta = service.generar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 9, 26)));

        ArgumentCaptor<BigDecimal> monto = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<BigDecimal> base = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<LocalDate> fecha = ArgumentCaptor.forClass(LocalDate.class);
        verify(ajustes).insertarRecargoSiAusente(eq(30L), monto.capture(), base.capture(),
                eq(new BigDecimal("10.0000")), anyString(), nullable(Long.class), fecha.capture(),
                eq(40L), eq("RECARGO:40:30:0"));
        assertThat(respuesta.recargosGenerados()).isEqualTo(1);
        assertThat(monto.getValue()).isEqualByComparingTo("90.00");
        assertThat(base.getValue()).isEqualByComparingTo("900.00");
        assertThat(fecha.getValue()).isEqualTo(LocalDate.of(2026, 9, 26));
    }

    @Test
    void limitaRecargosMensualesYProrrateaElUltimo() {
        Cargo cargo = cargo(new BigDecimal("1000.00"), LocalDate.of(2026, 1, 30));
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.MONTO_FIJO,
                PeriodicidadRecargo.MENSUAL, TipoLimiteRecargo.MONTO_FIJO);
        politica.setMontoFijo(new BigDecimal("40.00"));
        politica.setMoneda("MXN");
        politica.setValorLimite(new BigDecimal("100.00"));
        preparar(cargo, politica, BigDecimal.ZERO);
        when(ajustes.insertarRecargoSiAusente(anyLong(), any(), any(), isNull(), anyString(),
                nullable(Long.class), any(), anyLong(), anyString())).thenReturn(1);

        var respuesta = service.generar(new GeneracionRecargosRequest(1L, 2L,
                LocalDate.of(2026, 3, 31)));

        ArgumentCaptor<BigDecimal> montos = ArgumentCaptor.forClass(BigDecimal.class);
        ArgumentCaptor<LocalDate> fechas = ArgumentCaptor.forClass(LocalDate.class);
        verify(ajustes, times(3)).insertarRecargoSiAusente(eq(30L), montos.capture(),
                eq(new BigDecimal("1000.00")), isNull(), anyString(), nullable(Long.class),
                fechas.capture(), eq(40L), anyString());
        assertThat(respuesta.recargosGenerados()).isEqualTo(3);
        assertThat(montos.getAllValues()).containsExactly(
                new BigDecimal("40.00"), new BigDecimal("40.00"), new BigDecimal("20.00"));
        assertThat(fechas.getAllValues()).containsExactly(
                LocalDate.of(2026, 1, 31), LocalDate.of(2026, 2, 28), LocalDate.of(2026, 3, 31));
    }

    @Test
    void reportaClaveExistenteSinDuplicarElAcumulado() {
        Cargo cargo = cargo(new BigDecimal("500.00"), LocalDate.of(2026, 1, 1));
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.MONTO_FIJO,
                PeriodicidadRecargo.UNICA, TipoLimiteRecargo.SIN_LIMITE);
        politica.setMontoFijo(new BigDecimal("25.00"));
        politica.setMoneda("MXN");
        preparar(cargo, politica, BigDecimal.ZERO);

        var respuesta = service.generar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 1, 2)));

        assertThat(respuesta.recargosGenerados()).isZero();
        assertThat(respuesta.recargosExistentes()).isEqualTo(1);
    }

    @Test
    void omiteCargoCuandoLosDescuentosDejanLaBaseEnCero() {
        Cargo cargo = cargo(new BigDecimal("500.00"), LocalDate.of(2026, 1, 1));
        AjusteCargo descuento = new AjusteCargo();
        descuento.setTipo(TipoAjusteCargo.DESCUENTO);
        descuento.setEfecto(EfectoAjusteCargo.DISMINUCION);
        descuento.setMonto(new BigDecimal("500.00"));
        cargo.getAjustes().add(descuento);
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.PORCENTAJE,
                PeriodicidadRecargo.UNICA, TipoLimiteRecargo.SIN_LIMITE);
        politica.setPorcentaje(new BigDecimal("5.0000"));
        preparar(cargo, politica, BigDecimal.ZERO);

        var respuesta = service.generar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 1, 2)));

        assertThat(respuesta.cargosSinImporte()).isEqualTo(1);
        verify(ajustes, never()).insertarRecargoSiAusente(anyLong(), any(), any(), any(),
                anyString(), nullable(Long.class), any(), anyLong(), anyString());
    }

    private void preparar(Cargo cargo, PoliticaRecargo politica, BigDecimal acumulado) {
        when(cargos.buscarParaRecargo(anyLong(), nullable(Long.class), any(), anyLong(), any()))
                .thenReturn(new SliceImpl<>(List.of(cargo)));
        when(politicas.findByConceptoCobroIdAndActivoTrueAndGeneracionAutomaticaTrue(20L))
                .thenReturn(Optional.of(politica));
        when(ajustes.totalRecargosAutomaticos(30L, 40L)).thenReturn(acumulado);
    }

    private Cargo cargo(BigDecimal importe, LocalDate vencimiento) {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        Plantel plantel = new Plantel();
        plantel.setId(2L);
        plantel.setInstitucion(institucion);
        Alumno alumno = new Alumno();
        alumno.setId(3L);
        alumno.setInstitucion(institucion);
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(4L);
        inscripcion.setAlumno(alumno);
        inscripcion.setPlantel(plantel);
        ConceptoCobro concepto = new ConceptoCobro();
        concepto.setId(20L);
        concepto.setNombre("Colegiatura");
        concepto.setInstitucion(institucion);
        concepto.setActivo(true);
        concepto.setPermiteRecargo(true);
        Cargo cargo = new Cargo();
        cargo.setId(30L);
        cargo.setInscripcion(inscripcion);
        cargo.setConceptoCobro(concepto);
        cargo.setImporteOriginal(importe);
        cargo.setFechaVencimiento(vencimiento);
        cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);
        return cargo;
    }

    private PoliticaRecargo politica(Cargo cargo, ModalidadBeca modalidad,
                                      PeriodicidadRecargo periodicidad,
                                      TipoLimiteRecargo tipoLimite) {
        PoliticaRecargo politica = new PoliticaRecargo();
        politica.setId(40L);
        politica.setConceptoCobro(cargo.getConceptoCobro());
        politica.setModalidad(modalidad);
        politica.setPeriodicidad(periodicidad);
        politica.setTipoLimite(tipoLimite);
        politica.setActivo(true);
        politica.setGeneracionAutomatica(true);
        return politica;
    }
}
