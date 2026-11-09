package escuela.cobranza.service.impl;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.Grado;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.alumno.entity.Alumno;
import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.request.GeneracionCargosRequest;
import escuela.cobranza.entity.Cargo;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.EstadoRegistroCargo;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.cobranza.mapper.CargoMapper;
import escuela.cobranza.repository.CargoRepository;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CargoServiceImplTest {

    private final CargoRepository repository = mock(CargoRepository.class);
    private final CuotaAlumnoRepository cuotaRepository = mock(CuotaAlumnoRepository.class);
    private final InscripcionRepository inscripcionRepository = mock(InscripcionRepository.class);
    private final ConceptoCobroRepository conceptoRepository = mock(ConceptoCobroRepository.class);
    private final PeriodoAcademicoRepository periodoRepository = mock(PeriodoAcademicoRepository.class);
    private final AplicacionBecaCargoService aplicacionBecaService = mock(AplicacionBecaCargoService.class);
    private final escuela.cobranza.service.SeleccionGeneracionService selecciones=mock(escuela.cobranza.service.SeleccionGeneracionService.class);
    private final CargoServiceImpl service = new CargoServiceImpl(repository, cuotaRepository,
            inscripcionRepository, conceptoRepository, periodoRepository, new CargoMapper(),
            aplicacionBecaService,selecciones);

    @BeforeEach
    void sinBecaPorDefecto() {
        when(aplicacionBecaService.previsualizar(any(), any(), any(), any(), any()))
                .thenReturn(new AplicacionBecaCargoService.CalculoBeca(null, new BigDecimal("0.00")));
    }

    @Test
    void generaMensualidadesPorAlumnoYRecortaElDiaEnFebrero() {
        CuotaAlumno cuota = cuotaMensual();
        when(cuotaRepository.buscarParaGeneracion(eq(1L), isNull(),
                eq(LocalDate.of(2026, 2, 28)), eq(0L), any()))
                .thenReturn(new SliceImpl<>(List.of(cuota)));
        when(repository.clavesGeneradasPorCuota(90L)).thenReturn(List.of());
        when(repository.insertarAutomaticoSiAusente(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), isNull())).thenReturn(1);

        var resultado = service.generar(new GeneracionCargosRequest(
                1L, null, LocalDate.of(2026, 2, 28)));

        assertThat(resultado.cuotasRevisadas()).isEqualTo(1);
        assertThat(resultado.cargosGenerados()).isEqualTo(2);
        verify(repository).insertarAutomaticoSiAusente(eq(50L), eq(70L), eq(90L),
                eq("AUTO:1:90:2026-01"), any(), eq(LocalDate.of(2026, 1, 20)),
                eq(LocalDate.of(2026, 1, 31)), any(), eq(LocalDate.of(2026, 1, 31)),
                eq(new BigDecimal("3000.00")), eq("MXN"), isNull());
        verify(repository).insertarAutomaticoSiAusente(eq(50L), eq(70L), eq(90L),
                eq("AUTO:1:90:2026-02"), any(), eq(LocalDate.of(2026, 2, 1)),
                eq(LocalDate.of(2026, 2, 28)), any(), eq(LocalDate.of(2026, 2, 28)),
                eq(new BigDecimal("3000.00")), eq("MXN"), isNull());
    }

    @Test
    void reportaExistentesCuandoLaClaveIdempotenteYaFueInsertada() {
        CuotaAlumno cuota = cuotaMensual();
        cuota.setFechaFin(LocalDate.of(2026, 1, 31));
        when(cuotaRepository.buscarParaGeneracion(any(), any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(cuota)));
        when(repository.clavesGeneradasPorCuota(90L)).thenReturn(List.of());
        when(repository.insertarAutomaticoSiAusente(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), isNull())).thenReturn(0);

        var resultado = service.generar(new GeneracionCargosRequest(
                1L, null, LocalDate.of(2026, 1, 31)));

        assertThat(resultado.cargosGenerados()).isZero();
        assertThat(resultado.cargosYaExistentes()).isEqualTo(1);
        verify(repository, times(1)).insertarAutomaticoSiAusente(any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), isNull());
    }

    @Test
    void previsualizaSoloPeriodosFaltantesSinInsertar() {
        CuotaAlumno cuota = cuotaMensual();
        when(cuotaRepository.buscarParaGeneracion(eq(1L), isNull(),
                eq(LocalDate.of(2026, 2, 28)), eq(0L), any()))
                .thenReturn(new SliceImpl<>(List.of(cuota)));
        when(repository.clavesGeneradasPorCuota(90L))
                .thenReturn(List.of("AUTO:1:90:2026-01"));

        var vista = service.previsualizar(new GeneracionCargosRequest(
                1L, null, LocalDate.of(2026, 2, 28)), 0, 25);

        assertThat(vista.cuotasConPendientes()).isEqualTo(1);
        assertThat(vista.pagosPorGenerar()).isEqualTo(1);
        assertThat(vista.importeTotal()).isEqualByComparingTo("3000.00");
        assertThat(vista.becaTotal()).isEqualByComparingTo("0.00");
        assertThat(vista.importeNetoTotal()).isEqualByComparingTo("3000.00");
        assertThat(vista.pagina().getContent()).singleElement().satisfies(fila -> {
            assertThat(fila.alumnoNombre()).isEqualTo("Ana López");
            assertThat(fila.periodo()).contains("febrero 2026");
            assertThat(fila.fechaVencimiento()).isEqualTo(LocalDate.of(2026, 2, 28));
            assertThat(fila.descripcionBeca()).isEqualTo("Sin beca aplicable");
            assertThat(fila.importeNeto()).isEqualByComparingTo("3000.00");
        });
        verify(repository, times(0)).insertarAutomaticoSiAusente(any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void resumeBecasDeTodosLosPeriodosAunqueLaPaginaNoLosMuestre() {
        CuotaAlumno cuota = cuotaMensual();
        when(cuotaRepository.buscarParaGeneracion(any(), any(), any(), any(), any()))
                .thenReturn(new SliceImpl<>(List.of(cuota)));
        when(repository.clavesGeneradasPorCuota(90L)).thenReturn(List.of());
        when(aplicacionBecaService.previsualizar(eq(50L), eq(70L),
                eq(LocalDate.of(2026, 2, 1)), eq(LocalDate.of(2026, 2, 28)), any()))
                .thenReturn(new AplicacionBecaCargoService.CalculoBeca(null, new BigDecimal("600.00")));
        var vista = service.previsualizar(new GeneracionCargosRequest(
                1L, null, LocalDate.of(2026, 2, 28)), 1, 10);
        assertThat(vista.pagina().getContent()).isEmpty();
        assertThat(vista.pagosPorGenerar()).isEqualTo(2);
        assertThat(vista.importeTotal()).isEqualByComparingTo("6000.00");
        assertThat(vista.becaTotal()).isEqualByComparingTo("600.00");
        assertThat(vista.importeNetoTotal()).isEqualByComparingTo("5400.00");
        verify(repository, times(0)).insertarAutomaticoSiAusente(any(), any(), any(),
                any(), any(), any(), any(), any(), any(), any(), any(), any());
        verify(aplicacionBecaService, times(0)).aplicar(any());
    }

    @Test
    void generaCargoUnicoInmediatoConLaMismaClaveIdempotente() {
        CuotaAlumno cuota = cuotaMensual();
        cuota.setFrecuencia(FrecuenciaCuota.UNICA);
        cuota.setFechaFin(LocalDate.of(2026, 12, 31));
        cuota.setFechaVencimientoUnico(LocalDate.of(2026, 8, 15));
        Cargo cargo = cargo();
        cargo.setCuotaAlumno(cuota);
        cargo.setClaveGeneracion("AUTO:1:90:UNICA");
        when(cuotaRepository.findByIdForUpdate(90L)).thenReturn(Optional.of(cuota));
        when(repository.insertarAutomaticoSiAusente(any(), any(), any(), any(), any(),
                any(), any(), any(), any(), any(), any(), isNull())).thenReturn(0);
        when(repository.findByClaveGeneracion("AUTO:1:90:UNICA")).thenReturn(Optional.of(cargo));

        var respuesta = service.generarCargoUnico(90L);

        assertThat(respuesta.claveGeneracion()).isEqualTo("AUTO:1:90:UNICA");
        verify(repository).insertarAutomaticoSiAusente(eq(50L), eq(70L), eq(90L),
                eq("AUTO:1:90:UNICA"), any(), eq(LocalDate.of(2026, 1, 20)),
                eq(LocalDate.of(2026, 12, 31)), any(), eq(LocalDate.of(2026, 8, 15)),
                eq(new BigDecimal("3000.00")), eq("MXN"), isNull());
    }

    @Test
    void rechazaCargoManualDeOtraInstitucion() {
        Inscripcion inscripcion = inscripcion();
        ConceptoCobro concepto = concepto(2L);
        when(inscripcionRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(inscripcion));
        when(conceptoRepository.findById(70L)).thenReturn(Optional.of(concepto));
        CargoManualRequest request = new CargoManualRequest(50L, 70L, "Material",
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 31), null,
                LocalDate.of(2026, 1, 20), null, LocalDate.of(2026, 1, 31),
                new BigDecimal("500.00"), "MXN");

        assertThatThrownBy(() -> service.crearManual(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("misma institución");
    }

    @Test
    void cancelaSinBorrarYConservaElMotivo() {
        Cargo cargo = cargo();
        cargo.setVersion(3L);
        when(repository.findByIdForUpdate(101L)).thenReturn(Optional.of(cargo));
        when(repository.saveAndFlush(cargo)).thenReturn(cargo);

        var respuesta = service.cancelar(101L, 3L, "  Cobro capturado por error  ");

        assertThat(respuesta.estadoRegistro()).isEqualTo(EstadoRegistroCargo.CANCELADO);
        assertThat(respuesta.motivoCancelacion()).isEqualTo("Cobro capturado por error");
        assertThat(respuesta.saldoPendiente()).isEqualByComparingTo("0.00");
        assertThat(cargo.getCanceladoEn()).isNotNull();
    }

    @Test void diagnosticoExplicaCuotaUnicaGeneradaAunqueElCargoEsteCancelado() {
        var q=cuotaMensual();q.setFrecuencia(FrecuenciaCuota.UNICA);q.setFechaVencimientoUnico(LocalDate.of(2026,1,31));
        when(cuotaRepository.buscarParaDiagnostico(eq(1L),isNull(),eq(0L),any())).thenReturn(new SliceImpl<>(List.of(q)));
        when(repository.clavesGeneradasPorCuota(90L)).thenReturn(List.of("AUTO:1:90:UNICA"));when(cuotaRepository.ultimoCargo(90L)).thenReturn(101L);
        var pagina=service.diagnosticar(new GeneracionCargosRequest(1L,null,LocalDate.of(2026,2,1)),0);
        assertThat(pagina.getTotalElements()).isEqualTo(1);
        assertThat(pagina.getContent().getFirst().motivo()).contains("Cancelar su cargo no la regenera");
        assertThat(pagina.getContent().getFirst().cargoId()).isEqualTo(101L);
        verify(repository,org.mockito.Mockito.never()).insertarAutomaticoSiAusente(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any());
    }
    @Test void seleccionIndividualGeneraSoloElMesMarcadoYNoNuevosPeriodosFueraDeLaVista() {
        var cuota=cuotaMensual();cuota.setVersion(0L);var token=java.util.UUID.randomUUID();
        var corte=LocalDate.of(2026,2,28);var clave="AUTO:1:90:2026-02";
        when(cuotaRepository.buscarParaGeneracion(any(),any(),any(),any(),any())).thenReturn(new SliceImpl<>(List.of(cuota)));
        when(repository.clavesGeneradasPorCuota(90L)).thenReturn(List.of());
        when(selecciones.items(eq(token),any())).thenReturn(java.util.Map.of(clave,new escuela.cobranza.service.SeleccionGeneracionService.Item(clave,0L,new BigDecimal("3000"),corte)));
        when(selecciones.confirmar(any(),any(),any(),any(),any(),any(),any())).thenReturn(1L);
        var emitido=new Cargo();emitido.setImporteOriginal(new BigDecimal("3000"));emitido.setFechaVencimiento(corte);
        when(repository.findByClaveGeneracion(clave)).thenReturn(Optional.of(emitido));
        when(repository.insertarAutomaticoSiAusente(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
        var resultado=service.generar(new GeneracionCargosRequest(1L,null,corte,token,java.util.Set.of(),java.util.Set.of(clave)));
        assertThat(resultado.cargosGenerados()).isEqualTo(1);
        verify(repository,times(1)).insertarAutomaticoSiAusente(any(),any(),any(),eq(clave),any(),any(),any(),any(),any(),any(),any(),any());
        verify(selecciones).comprobarCantidad(1L,1L);verify(selecciones).consumida(token);
    }
    @Test void desmarcarUnMesLoOmiteSinCancelarLaCuota() {
        var cuota=cuotaMensual();when(cuotaRepository.buscarParaGeneracion(any(),any(),any(),any(),any())).thenReturn(new SliceImpl<>(List.of(cuota)));
        when(repository.insertarAutomaticoSiAusente(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenReturn(1);
        var r=service.generar(new GeneracionCargosRequest(1L,null,LocalDate.of(2026,2,28),null,java.util.Set.of("AUTO:1:90:2026-01"),null));
        assertThat(r.cargosGenerados()).isEqualTo(1);assertThat(cuota.getEstado()).isEqualTo(EstadoCuota.ACTIVA);
    }
    private void autorizarIndividual() {
        var permisos=java.util.stream.Stream.of("INSCRIPCION_ADMINISTRAR","CARGO_ADMINISTRAR","PAGO_REGISTRAR").map(org.springframework.security.core.authority.SimpleGrantedAuthority::new).toList();
        var principal=new escuela.seguridad.service.UsuarioPrincipal(1L,1L,java.util.Set.of(20L),false,false,"qa","",permisos);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(principal,"",permisos));
    }
    @Test void vistaIndividualNoEscribeYConfirmacionGeneraSoloElMesElegido() {
        autorizarIndividual();try {
            var q=cuotaMensual();q.setVersion(0L);q.getInscripcion().getAlumno().setActivo(true);
            when(inscripcionRepository.findById(50L)).thenReturn(Optional.of(q.getInscripcion()));
            when(inscripcionRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(q.getInscripcion()));
            when(cuotaRepository.findAllByInscripcionIdOrderByCreadoEnDescIdDesc(50L)).thenReturn(List.of(q));
            when(cuotaRepository.findById(90L)).thenReturn(Optional.of(q));
            var emitido=new java.util.concurrent.atomic.AtomicReference<Cargo>();
            when(repository.findByClaveGeneracion(any())).thenAnswer(a->Optional.ofNullable(emitido.get()));
            var desde=LocalDate.of(2026,1,1);var hasta=LocalDate.of(2026,2,28);
            var vista=service.anticipables(50L,desde,hasta);assertThat(vista).hasSize(2);
            org.mockito.Mockito.verify(repository,org.mockito.Mockito.never()).insertarAutomaticoSiAusente(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any());
            when(repository.insertarAutomaticoSiAusente(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any())).thenAnswer(a->{var c=cargo();c.setId(201L);c.setImporteOriginal(new BigDecimal("3000.00"));c.setPeriodoCobroInicio(LocalDate.of(2026,2,1));c.setPeriodoCobroFin(hasta);c.setFechaVencimiento(hasta);emitido.set(c);return 1;});
            var salida=service.confirmarAnticipo(50L,desde,hasta,vista,java.util.Set.of("AUTO:1:90:2026-02"));
            assertThat(salida).hasSize(1);assertThat(salida.getFirst().id()).isEqualTo(201L);
            verify(repository).insertarAutomaticoSiAusente(eq(50L),any(),eq(90L),eq("AUTO:1:90:2026-02"),any(),any(),any(),any(),any(),any(),any(),any());
            org.mockito.Mockito.verify(cuotaRepository,org.mockito.Mockito.never()).buscarParaGeneracion(any(),any(),any(),any(),any());
        }finally{org.springframework.security.core.context.SecurityContextHolder.clearContext();}
    }
    @Test void anticipoRechazaVistaObsoletaYClavesDeOtroAlumno() {
        autorizarIndividual();try {
            var q=cuotaMensual();q.setVersion(0L);q.getInscripcion().getAlumno().setActivo(true);
            when(inscripcionRepository.findById(50L)).thenReturn(Optional.of(q.getInscripcion()));
            when(inscripcionRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(q.getInscripcion()));
            when(cuotaRepository.findAllByInscripcionIdOrderByCreadoEnDescIdDesc(50L)).thenReturn(List.of(q));
            var desde=LocalDate.of(2026,1,1);var hasta=LocalDate.of(2026,2,28);var vista=service.anticipables(50L,desde,hasta);
            assertThatThrownBy(()->service.confirmarAnticipo(50L,desde,hasta,vista,java.util.Set.of("AUTO:1:999:2026-02"))).hasMessageContaining("no pertenece");
            q.setImporteBase(new BigDecimal("3100.00"));
            assertThatThrownBy(()->service.confirmarAnticipo(50L,desde,hasta,vista,java.util.Set.of("AUTO:1:90:2026-02"))).hasMessageContaining("Cambió");
            org.mockito.Mockito.verify(repository,org.mockito.Mockito.never()).insertarAutomaticoSiAusente(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any());
        }finally{org.springframework.security.core.context.SecurityContextHolder.clearContext();}
    }
    @Test void anticipoReconoceUnCargoManualDelMismoConceptoYPeriodoSinDuplicarlo() {
        autorizarIndividual();try {
            var q=cuotaMensual();q.getInscripcion().getAlumno().setActivo(true);
            when(inscripcionRepository.findById(50L)).thenReturn(Optional.of(q.getInscripcion()));
            when(cuotaRepository.findAllByInscripcionIdOrderByCreadoEnDescIdDesc(50L)).thenReturn(List.of(q));
            when(repository.findAllByInscripcionIdOrderByFechaVencimientoDescIdDesc(50L)).thenReturn(List.of(cargo()));
            var filas=service.anticipables(50L,LocalDate.of(2026,1,1),LocalDate.of(2026,1,31));
            assertThat(filas).hasSize(1);assertThat(filas.getFirst().cargoId()).isEqualTo(101L);assertThat(filas.getFirst().estado()).isEqualTo("Existente");
            assertThat(filas.getFirst().importe()).isEqualByComparingTo("500");
        }finally{org.springframework.security.core.context.SecurityContextHolder.clearContext();}
    }
    private CuotaAlumno cuotaMensual() {
        CuotaAlumno cuota = new CuotaAlumno();
        cuota.setId(90L);
        cuota.setInscripcion(inscripcion());
        cuota.setConceptoCobro(concepto(1L));
        cuota.setImporteBase(new BigDecimal("3000.00"));
        cuota.setMoneda("MXN");
        cuota.setFrecuencia(FrecuenciaCuota.MENSUAL);
        cuota.setFechaInicio(LocalDate.of(2026, 1, 20));
        cuota.setFechaFin(LocalDate.of(2026, 2, 28));
        cuota.setDiaVencimiento(31);
        cuota.setGeneracionAutomatica(true);
        cuota.setEstado(EstadoCuota.ACTIVA);
        return cuota;
    }

    private Cargo cargo() {
        Cargo cargo = new Cargo();
        cargo.setId(101L);
        cargo.setInscripcion(inscripcion());
        cargo.setConceptoCobro(concepto(1L));
        cargo.setClaveGeneracion("MANUAL:1:abc");
        cargo.setDescripcion("Material");
        cargo.setPeriodoCobroInicio(LocalDate.of(2026, 1, 20));
        cargo.setPeriodoCobroFin(LocalDate.of(2026, 1, 31));
        cargo.setFechaEmision(LocalDate.of(2026, 1, 20));
        cargo.setFechaVencimiento(LocalDate.of(2026, 1, 31));
        cargo.setImporteOriginal(new BigDecimal("500.00"));
        cargo.setMoneda("MXN");
        cargo.setEstadoRegistro(EstadoRegistroCargo.EMITIDO);
        return cargo;
    }

    private Inscripcion inscripcion() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        institucion.setMonedaPredeterminada("MXN");
        Alumno alumno = new Alumno();
        alumno.setId(10L);
        alumno.setInstitucion(institucion);
        alumno.setMatricula("A-001");
        alumno.setNombres("Ana");
        alumno.setPrimerApellido("López");
        Plantel plantel = new Plantel();
        plantel.setId(20L);
        plantel.setNombre("Centro");
        plantel.setInstitucion(institucion);
        NivelEducativo nivel = new NivelEducativo();
        nivel.setId(30L);
        nivel.setInstitucion(institucion);
        Grado grado = new Grado();
        grado.setId(40L);
        grado.setNivelEducativo(nivel);
        CicloEscolar ciclo = new CicloEscolar();
        ciclo.setId(45L);
        ciclo.setFechaInicio(LocalDate.of(2026, 1, 1));
        ciclo.setFechaFin(LocalDate.of(2026, 12, 31));
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(50L);
        inscripcion.setAlumno(alumno);
        inscripcion.setPlantel(plantel);
        inscripcion.setGrado(grado);
        inscripcion.setCicloEscolar(ciclo);
        inscripcion.setNumeroInscripcion("INS-001");
        inscripcion.setFechaInicio(LocalDate.of(2026, 1, 1));
        inscripcion.setFechaFin(LocalDate.of(2026, 12, 31));
        inscripcion.setEstado(EstadoInscripcion.ACTIVA);
        return inscripcion;
    }

    private ConceptoCobro concepto(Long institucionId) {
        Institucion institucion = new Institucion();
        institucion.setId(institucionId);
        ConceptoCobro concepto = new ConceptoCobro();
        concepto.setId(70L);
        concepto.setInstitucion(institucion);
        concepto.setCodigo("COL");
        concepto.setNombre("Colegiatura");
        concepto.setActivo(true);
        return concepto;
    }
}
