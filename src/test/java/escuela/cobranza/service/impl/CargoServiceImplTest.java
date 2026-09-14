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
    private final CargoServiceImpl service = new CargoServiceImpl(repository, cuotaRepository,
            inscripcionRepository, conceptoRepository, periodoRepository, new CargoMapper());

    @Test
    void generaMensualidadesPorAlumnoYRecortaElDiaEnFebrero() {
        CuotaAlumno cuota = cuotaMensual();
        when(cuotaRepository.buscarParaGeneracion(eq(1L), isNull(),
                eq(LocalDate.of(2026, 2, 28)), eq(0L), any()))
                .thenReturn(new SliceImpl<>(List.of(cuota)));
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
    void rechazaCargoManualDeOtraInstitucion() {
        Inscripcion inscripcion = inscripcion();
        ConceptoCobro concepto = concepto(2L);
        when(inscripcionRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(inscripcion));
        when(conceptoRepository.findById(70L)).thenReturn(Optional.of(concepto));
        CargoManualRequest request = new CargoManualRequest(50L, 70L, "Material",
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 31), null,
                LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 31),
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
