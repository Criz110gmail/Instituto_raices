package escuela.cobranza.service.impl;

import escuela.alumno.entity.Alumno;
import escuela.cobranza.entity.*;
import escuela.cobranza.mapper.PoliticaRecargoMapper;
import escuela.cobranza.repository.*;
import escuela.cobranza.dto.request.GeneracionRecargosRequest;
import escuela.inscripcion.entity.Inscripcion;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.finanzas.entity.AplicacionPago;
import escuela.finanzas.entity.OperacionAplicacionPago;
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
    private final escuela.cobranza.service.SeleccionGeneracionService selecciones=mock(escuela.cobranza.service.SeleccionGeneracionService.class);
    private final PoliticaRecargoServiceImpl service = new PoliticaRecargoServiceImpl(
            politicas, conceptos, cargos, ajustes, new PoliticaRecargoMapper(),selecciones);

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
    void ejemploConBecaYTopeGeneraOchentaYSetentaNoOtrosOchenta() {
        Cargo cargo = cargo(new BigDecimal("1000.00"), LocalDate.of(2026, 7, 20));
        AjusteCargo beca = new AjusteCargo(); beca.setTipo(TipoAjusteCargo.BECA);
        beca.setEfecto(EfectoAjusteCargo.DISMINUCION); beca.setMonto(new BigDecimal("200.00"));
        cargo.getAjustes().add(beca);
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.PORCENTAJE,
                PeriodicidadRecargo.MENSUAL, TipoLimiteRecargo.MONTO_FIJO);
        politica.setPorcentaje(new BigDecimal("10")); politica.setDiasGracia(0);
        politica.setValorLimite(new BigDecimal("150.00"));
        preparar(cargo, politica, BigDecimal.ZERO);
        when(ajustes.insertarRecargoSiAusente(anyLong(), any(), any(), any(), anyString(),
                nullable(Long.class), any(), anyLong(), anyString())).thenReturn(1);
        service.generar(new GeneracionRecargosRequest(1L, 2L, LocalDate.of(2026, 9, 21)));
        ArgumentCaptor<BigDecimal> montos = ArgumentCaptor.forClass(BigDecimal.class);
        verify(ajustes,times(2)).insertarRecargoSiAusente(eq(30L),montos.capture(),
                eq(new BigDecimal("800.00")),eq(new BigDecimal("10")),anyString(),
                nullable(Long.class),any(),eq(40L),anyString());
        assertThat(montos.getAllValues()).containsExactly(new BigDecimal("80.00"),new BigDecimal("70.00"));
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

    @Test
    void previsualizaSinModificarYResumeElNuevoSaldo() {
        Cargo cargo = cargo(new BigDecimal("1000.00"), LocalDate.of(2026, 9, 20));
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.PORCENTAJE,
                PeriodicidadRecargo.UNICA, TipoLimiteRecargo.SIN_LIMITE);
        politica.setPorcentaje(new BigDecimal("5.0000"));
        preparar(cargo, politica, BigDecimal.ZERO);

        var vista = service.previsualizar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 9, 25)), 0, 25);

        assertThat(vista.cargosAplicables()).isEqualTo(1);
        assertThat(vista.recargosNuevos()).isEqualTo(1);
        assertThat(vista.totalRecargos()).isEqualByComparingTo("50.00");
        assertThat(vista.nuevoSaldo()).isEqualByComparingTo("1050.00");
        assertThat(vista.pagina().getContent()).singleElement().satisfies(fila -> {
            assertThat(fila.diasAtraso()).isEqualTo(5);
            assertThat(fila.politica()).contains("5 %");
        });
        verify(ajustes, never()).insertarRecargoSiAusente(anyLong(), any(), any(), any(),
                anyString(), nullable(Long.class), any(), anyLong(), anyString());
    }

    @Test
    void niPrevisualizaNiRecargaUnCargoCompletamentePagado() {
        Cargo cargo = cargo(new BigDecimal("1000.00"), LocalDate.of(2026, 9, 20));
        AplicacionPago aplicacion = new AplicacionPago();
        aplicacion.setMonto(new BigDecimal("1000.00"));
        aplicacion.setOperacion(OperacionAplicacionPago.APLICAR);
        cargo.getAplicaciones().add(aplicacion);
        PoliticaRecargo politica = politica(cargo, ModalidadBeca.PORCENTAJE,
                PeriodicidadRecargo.UNICA, TipoLimiteRecargo.SIN_LIMITE);
        politica.setPorcentaje(new BigDecimal("5.0000"));
        preparar(cargo, politica, BigDecimal.ZERO);

        var vista = service.previsualizar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 9, 25)), 0, 25);
        var generacion = service.generar(new GeneracionRecargosRequest(1L, null,
                LocalDate.of(2026, 9, 25)));

        assertThat(vista.cargosAplicables()).isZero();
        assertThat(generacion.recargosGenerados()).isZero();
        verify(ajustes, never()).insertarRecargoSiAusente(anyLong(), any(), any(), any(),
                anyString(), nullable(Long.class), any(), anyLong(), anyString());
    }

    @Test void noAplicaRecargoAlCargoDesmarcado() {
        var cargo=cargo(new BigDecimal("1000"),LocalDate.of(2026,9,20));
        var p=politica(cargo,ModalidadBeca.PORCENTAJE,PeriodicidadRecargo.UNICA,TipoLimiteRecargo.SIN_LIMITE);p.setPorcentaje(new BigDecimal("10"));preparar(cargo,p,BigDecimal.ZERO);
        var r=service.generar(new GeneracionRecargosRequest(1L,null,LocalDate.of(2026,10,6),null,java.util.Set.of("RECARGO:30"),null));
        assertThat(r.recargosGenerados()).isZero();verify(ajustes,never()).insertarRecargoSiAusente(anyLong(),any(),any(),any(),anyString(),nullable(Long.class),any(),anyLong(),anyString());
    }
    @Test void incluyeSoloCargoRevisadoYConsumeLaVistaUnaVez() {
        var cargo=cargo(new BigDecimal("1000"),LocalDate.of(2026,9,20));cargo.setVersion(0L);
        var p=politica(cargo,ModalidadBeca.PORCENTAJE,PeriodicidadRecargo.UNICA,TipoLimiteRecargo.SIN_LIMITE);p.setPorcentaje(new BigDecimal("10"));preparar(cargo,p,BigDecimal.ZERO);
        var token=java.util.UUID.randomUUID();var clave="RECARGO:30";
        when(selecciones.items(eq(token),any())).thenReturn(java.util.Map.of(clave,new escuela.cobranza.service.SeleccionGeneracionService.Item(clave,0L,new BigDecimal("100"),cargo.getFechaVencimiento())));
        when(selecciones.confirmar(any(),any(),any(),any(),any(),any(),any())).thenReturn(1L);
        when(ajustes.insertarRecargoSiAusente(anyLong(),any(),any(),any(),anyString(),nullable(Long.class),any(),anyLong(),anyString())).thenReturn(1);
        var r=service.generar(new GeneracionRecargosRequest(1L,null,LocalDate.of(2026,10,6),token,java.util.Set.of(),java.util.Set.of(clave)));
        assertThat(r.recargosGenerados()).isEqualTo(1);verify(selecciones).comprobarCantidad(1L,1L);verify(selecciones).consumida(token);
    }
    private void preparar(Cargo cargo, PoliticaRecargo politica, BigDecimal acumulado) {
        when(cargos.buscarParaRecargo(anyLong(), nullable(Long.class), any(), anyLong(), any()))
                .thenReturn(new SliceImpl<>(List.of(cargo)));
        when(politicas.findByConceptoCobroIdAndActivoTrueAndGeneracionAutomaticaTrue(20L))
                .thenReturn(Optional.of(politica));
        when(ajustes.totalRecargosAutomaticos(30L, 40L)).thenReturn(acumulado);
        when(ajustes.clavesDeRecargosAutomaticos(30L, 40L)).thenReturn(List.of());
    }

    private Cargo cargo(BigDecimal importe, LocalDate vencimiento) {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        Plantel plantel = new Plantel();
        plantel.setId(2L);
        plantel.setNombre("Plantel Centro");
        plantel.setInstitucion(institucion);
        Alumno alumno = new Alumno();
        alumno.setId(3L);
        alumno.setMatricula("ALU-001");
        alumno.setNombres("Ana");
        alumno.setPrimerApellido("López");
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
