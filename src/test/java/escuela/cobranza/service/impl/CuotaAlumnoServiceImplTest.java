package escuela.cobranza.service.impl;

import escuela.academico.entity.CicloEscolar;
import escuela.alumno.entity.Alumno;
import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.entity.CategoriaConceptoCobro;
import escuela.cobranza.entity.ConceptoCobro;
import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.cobranza.mapper.CuotaAlumnoMapper;
import escuela.cobranza.repository.ConceptoCobroRepository;
import escuela.cobranza.repository.CuotaAlumnoRepository;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CuotaAlumnoServiceImplTest {

    private final CuotaAlumnoRepository repository = mock(CuotaAlumnoRepository.class);
    private final InscripcionRepository inscripcionRepository = mock(InscripcionRepository.class);
    private final ConceptoCobroRepository conceptoRepository = mock(ConceptoCobroRepository.class);
    private final CuotaAlumnoServiceImpl service = new CuotaAlumnoServiceImpl(repository,
            inscripcionRepository, conceptoRepository, new CuotaAlumnoMapper());

    @Test
    void creaCuotaMensualIndividualConGeneracionAutomatica() {
        prepararRelaciones(1L, 1L);

        var respuesta = service.crear(mensual(EstadoCuota.ACTIVA, true));

        assertThat(respuesta.inscripcionId()).isEqualTo(10L);
        assertThat(respuesta.alumnoMatricula()).isEqualTo("A-001");
        assertThat(respuesta.importeBase()).isEqualByComparingTo("3000.00");
        assertThat(respuesta.diaVencimiento()).isEqualTo(10);
        assertThat(respuesta.generacionAutomatica()).isTrue();
    }

    @Test
    void mantieneCuotasSeparadasPorInscripcionYAlumno() {
        prepararRelaciones(1L, 1L);

        service.crear(mensual(EstadoCuota.ACTIVA, false));

        org.mockito.Mockito.verify(repository).buscarSuperpuestas(10L, 20L, 0L,
                EstadoCuota.ACTIVA, LocalDate.of(2026, 9, 1), LocalDate.of(2027, 6, 30));
    }

    @Test
    void rechazaConceptoDeOtraInstitucion() {
        prepararRelaciones(1L, 2L);

        assertThatThrownBy(() -> service.crear(mensual(EstadoCuota.ACTIVA, false)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("misma institución");
    }

    @Test
    void rechazaCuotaActivaSuperpuestaDelMismoConcepto() {
        prepararRelaciones(1L, 1L);
        when(repository.buscarSuperpuestas(any(), any(), any(), any(), any(), any()))
                .thenReturn(List.of(new CuotaAlumno()));

        assertThatThrownBy(() -> service.crear(mensual(EstadoCuota.ACTIVA, false)))
                .isInstanceOf(RecursoDuplicadoException.class);
    }

    @Test
    void cobroUnicoExigeFechaExactaDeVencimiento() {
        prepararRelaciones(1L, 1L);
        CuotaAlumnoRequest unica = new CuotaAlumnoRequest(10L, 20L,
                new BigDecimal("850.00"), "MXN", FrecuenciaCuota.UNICA,
                LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30),
                null, null, false, "Material extra", EstadoCuota.ACTIVA, null);

        assertThatThrownBy(() -> service.crear(unica))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("fecha exacta");
    }

    @Test
    void cuotaSuspendidaNoPuedeGenerarAutomaticamente() {
        prepararRelaciones(1L, 1L);

        assertThatThrownBy(() -> service.crear(mensual(EstadoCuota.SUSPENDIDA, true)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Sólo una cuota activa");
    }

    @Test
    void cuotaFinalizadaNoPuedeReactivarse() {
        Inscripcion inscripcion = inscripcion(1L);
        ConceptoCobro concepto = concepto(1L);
        CuotaAlumno cuota = new CuotaAlumno();
        cuota.setId(30L);
        cuota.setVersion(1L);
        cuota.setInscripcion(inscripcion);
        cuota.setConceptoCobro(concepto);
        cuota.setEstado(EstadoCuota.FINALIZADA);
        when(repository.findByIdForUpdate(30L)).thenReturn(Optional.of(cuota));

        CuotaAlumnoRequest request = mensual(EstadoCuota.ACTIVA, false);
        request = new CuotaAlumnoRequest(request.inscripcionId(), request.conceptoCobroId(),
                request.importeBase(), request.moneda(), request.frecuencia(), request.fechaInicio(),
                request.fechaFin(), request.diaVencimiento(), null, false, null,
                EstadoCuota.ACTIVA, 1L);

        CuotaAlumnoRequest finalRequest = request;
        assertThatThrownBy(() -> service.actualizar(30L, finalRequest))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("reactivarse");
    }

    private void prepararRelaciones(Long institucionInscripcion, Long institucionConcepto) {
        when(inscripcionRepository.findByIdForUpdate(10L))
                .thenReturn(Optional.of(inscripcion(institucionInscripcion)));
        when(conceptoRepository.findById(20L))
                .thenReturn(Optional.of(concepto(institucionConcepto)));
        when(repository.saveAndFlush(any())).thenAnswer(invocacion -> {
            CuotaAlumno cuota = invocacion.getArgument(0);
            cuota.setId(30L);
            cuota.setVersion(0L);
            return cuota;
        });
    }

    private CuotaAlumnoRequest mensual(EstadoCuota estado, boolean automatica) {
        return new CuotaAlumnoRequest(10L, 20L, new BigDecimal("3000.00"), "mxn",
                FrecuenciaCuota.MENSUAL, LocalDate.of(2026, 9, 1),
                LocalDate.of(2027, 6, 30), 10, null, automatica,
                null, estado, null);
    }

    private Inscripcion inscripcion(Long institucionId) {
        Institucion institucion = new Institucion();
        institucion.setId(institucionId);
        Alumno alumno = new Alumno();
        alumno.setId(5L);
        alumno.setInstitucion(institucion);
        alumno.setMatricula("A-001");
        alumno.setNombres("Ana");
        alumno.setPrimerApellido("López");
        Plantel plantel = new Plantel();
        plantel.setId(7L);
        plantel.setNombre("Centro");
        plantel.setInstitucion(institucion);
        CicloEscolar ciclo = new CicloEscolar();
        ciclo.setFechaInicio(LocalDate.of(2026, 8, 1));
        ciclo.setFechaFin(LocalDate.of(2027, 7, 31));
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setId(10L);
        inscripcion.setAlumno(alumno);
        inscripcion.setPlantel(plantel);
        inscripcion.setCicloEscolar(ciclo);
        inscripcion.setNumeroInscripcion("INS-001");
        inscripcion.setFechaInicio(LocalDate.of(2026, 8, 1));
        inscripcion.setFechaFin(LocalDate.of(2027, 7, 31));
        inscripcion.setEstado(EstadoInscripcion.ACTIVA);
        return inscripcion;
    }

    private ConceptoCobro concepto(Long institucionId) {
        Institucion institucion = new Institucion();
        institucion.setId(institucionId);
        ConceptoCobro concepto = new ConceptoCobro();
        concepto.setId(20L);
        concepto.setInstitucion(institucion);
        concepto.setCodigo("COLEG");
        concepto.setNombre("Colegiatura");
        concepto.setCategoria(CategoriaConceptoCobro.COLEGIATURA);
        concepto.setActivo(true);
        return concepto;
    }
}
