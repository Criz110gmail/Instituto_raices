package escuela.inscripcion.service.impl;

import escuela.academico.entity.CicloEscolar;
import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.Grado;
import escuela.academico.entity.Grupo;
import escuela.academico.entity.NivelEducativo;
import escuela.academico.entity.Turno;
import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.alumno.entity.Alumno;
import escuela.alumno.repository.AlumnoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.dto.request.AsignacionGrupoRequest;
import escuela.inscripcion.dto.request.InscripcionRequest;
import escuela.inscripcion.entity.AsignacionGrupo;
import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import escuela.inscripcion.mapper.AsignacionGrupoMapper;
import escuela.inscripcion.mapper.InscripcionMapper;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
import escuela.inscripcion.repository.InscripcionRepository;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.institucion.repository.PlantelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InscripcionServiceImplTest {

    private final InscripcionRepository repository = mock(InscripcionRepository.class);
    private final AsignacionGrupoRepository asignacionRepository = mock(AsignacionGrupoRepository.class);
    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final PlantelRepository plantelRepository = mock(PlantelRepository.class);
    private final CicloEscolarRepository cicloRepository = mock(CicloEscolarRepository.class);
    private final GradoRepository gradoRepository = mock(GradoRepository.class);
    private final GrupoRepository grupoRepository = mock(GrupoRepository.class);
    private final PlantelNivelRepository ofertaRepository = mock(PlantelNivelRepository.class);
    private final InstitucionRepository institucionRepository = mock(InstitucionRepository.class);
    private final InscripcionServiceImpl service = new InscripcionServiceImpl(repository,
            asignacionRepository, alumnoRepository, plantelRepository, cicloRepository,
            gradoRepository, grupoRepository, ofertaRepository, institucionRepository,
            new InscripcionMapper(), new AsignacionGrupoMapper());

    private Institucion institucion;
    private Alumno alumno;
    private Plantel plantel;
    private CicloEscolar ciclo;
    private Grado grado;

    @BeforeEach
    void preparar() {
        institucion = new Institucion();
        institucion.setId(1L);
        institucion.setActivo(true);
        alumno = new Alumno();
        alumno.setId(10L);
        alumno.setInstitucion(institucion);
        alumno.setMatricula("A-10");
        alumno.setNombres("Ana");
        alumno.setPrimerApellido("López");
        alumno.setActivo(true);
        plantel = new Plantel();
        plantel.setId(20L);
        plantel.setInstitucion(institucion);
        plantel.setNombre("Centro");
        plantel.setActivo(true);
        NivelEducativo nivel = new NivelEducativo();
        nivel.setId(30L);
        nivel.setInstitucion(institucion);
        nivel.setNombre("Primaria");
        nivel.setActivo(true);
        grado = new Grado();
        grado.setId(40L);
        grado.setNivelEducativo(nivel);
        grado.setNombre("Primero");
        grado.setActivo(true);
        ciclo = new CicloEscolar();
        ciclo.setId(50L);
        ciclo.setInstitucion(institucion);
        ciclo.setNombre("2026-2027");
        ciclo.setFechaInicio(LocalDate.of(2026, 8, 20));
        ciclo.setFechaFin(LocalDate.of(2027, 7, 10));
        ciclo.setEstado(EstadoAcademico.ABIERTO);

        when(alumnoRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(alumno));
        when(institucionRepository.buscarPorIdConBloqueo(1L)).thenReturn(Optional.of(institucion));
        when(plantelRepository.findById(20L)).thenReturn(Optional.of(plantel));
        when(cicloRepository.findById(50L)).thenReturn(Optional.of(ciclo));
        when(gradoRepository.findById(40L)).thenReturn(Optional.of(grado));
        when(ofertaRepository.existsByPlantelIdAndNivelEducativoIdAndActivoTrue(20L, 30L))
                .thenReturn(true);
        when(repository.buscarSuperpuestas(anyLong(), anyLong(), any(), any(), any()))
                .thenReturn(List.of());
        when(repository.saveAndFlush(any())).thenAnswer(invocacion -> {
            Inscripcion entidad = invocacion.getArgument(0);
            entidad.setId(70L);
            entidad.setVersion(0L);
            return entidad;
        });
        when(asignacionRepository.saveAndFlush(any())).thenAnswer(invocacion -> {
            AsignacionGrupo entidad = invocacion.getArgument(0);
            entidad.setId(80L);
            entidad.setVersion(0L);
            return entidad;
        });
    }

    @Test
    void creaInscripcionNormalizadaConRelacionesValidas() {
        var respuesta = service.crear(request(EstadoInscripcion.ACTIVA, null, null, null));

        assertThat(respuesta.numeroInscripcion()).isEqualTo("INS-001");
        assertThat(respuesta.alumnoId()).isEqualTo(10L);
        assertThat(respuesta.plantelId()).isEqualTo(20L);
        verify(institucionRepository).buscarPorIdConBloqueo(1L);
    }

    @Test
    void rechazaRelacionesDeOtraInstitucion() {
        Institucion ajena = new Institucion();
        ajena.setId(2L);
        plantel.setInstitucion(ajena);

        assertThatThrownBy(() -> service.crear(request(EstadoInscripcion.ACTIVA,
                null, null, null))).isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("misma institución");
    }

    @Test
    void continuidadCierraInscripcionYGrupoAnterior() {
        Inscripcion anterior = inscripcion(90L, EstadoInscripcion.ACTIVA);
        AsignacionGrupo asignacion = new AsignacionGrupo();
        asignacion.setInscripcion(anterior);
        asignacion.setFechaInicio(LocalDate.of(2026, 8, 20));
        when(repository.findByIdForUpdate(90L)).thenReturn(Optional.of(anterior));
        when(asignacionRepository.findAbiertaForUpdate(90L)).thenReturn(Optional.of(asignacion));

        service.crear(request(EstadoInscripcion.PREINSCRITA, null, 90L,
                LocalDate.of(2026, 10, 1)));

        assertThat(anterior.getEstado()).isEqualTo(EstadoInscripcion.FINALIZADA);
        assertThat(anterior.getFechaFin()).isEqualTo(LocalDate.of(2026, 9, 30));
        assertThat(asignacion.getFechaFin()).isEqualTo(LocalDate.of(2026, 9, 30));
    }

    @Test
    void rechazaGrupoSinCapacidad() {
        Inscripcion inscripcion = inscripcion(70L, EstadoInscripcion.ACTIVA);
        Grupo grupo = grupo(100L, 1);
        when(repository.findByIdForUpdate(70L)).thenReturn(Optional.of(inscripcion));
        when(grupoRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(grupo));
        when(asignacionRepository.findAbiertaForUpdate(70L)).thenReturn(Optional.empty());
        when(asignacionRepository.contarSuperpuestas(100L, LocalDate.of(2026, 9, 1), null))
                .thenReturn(1L);

        assertThatThrownBy(() -> service.asignarGrupo(70L,
                new AsignacionGrupoRequest(100L, LocalDate.of(2026, 9, 1), null)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("capacidad");
    }

    @Test
    void cambioDeGrupoCierraAsignacionAnterior() {
        Inscripcion inscripcion = inscripcion(70L, EstadoInscripcion.ACTIVA);
        Grupo anteriorGrupo = grupo(99L, 20);
        Grupo nuevoGrupo = grupo(100L, 20);
        AsignacionGrupo anterior = new AsignacionGrupo();
        anterior.setInscripcion(inscripcion);
        anterior.setGrupo(anteriorGrupo);
        anterior.setFechaInicio(LocalDate.of(2026, 8, 20));
        when(repository.findByIdForUpdate(70L)).thenReturn(Optional.of(inscripcion));
        when(grupoRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(nuevoGrupo));
        when(asignacionRepository.findAbiertaForUpdate(70L)).thenReturn(Optional.of(anterior));

        service.asignarGrupo(70L, new AsignacionGrupoRequest(100L,
                LocalDate.of(2026, 10, 1), "Cambio de sección"));

        assertThat(anterior.getFechaFin()).isEqualTo(LocalDate.of(2026, 9, 30));
        assertThat(anterior.getMotivo()).isEqualTo("Cambio de sección");
    }

    @Test
    void noReactivaUnaInscripcionFinalizada() {
        Inscripcion entidad = inscripcion(70L, EstadoInscripcion.FINALIZADA);
        entidad.setFechaFin(LocalDate.of(2027, 7, 10));
        when(repository.findByIdForUpdate(70L)).thenReturn(Optional.of(entidad));

        assertThatThrownBy(() -> service.actualizar(70L,
                request(EstadoInscripcion.ACTIVA, 0L, null, null)))
                .isInstanceOf(ReglaNegocioException.class).hasMessageContaining("transición");
    }

    private InscripcionRequest request(EstadoInscripcion estado, Long version,
                                       Long anteriorId, LocalDate inicioPersonalizado) {
        LocalDate inicio = inicioPersonalizado == null ? LocalDate.of(2026, 9, 1) : inicioPersonalizado;
        LocalDate fin = switch (estado) {
            case BAJA, FINALIZADA, CANCELADA -> LocalDate.of(2027, 7, 10);
            default -> null;
        };
        String motivo = estado == EstadoInscripcion.BAJA || estado == EstadoInscripcion.CANCELADA
                ? "Motivo documentado" : null;
        return new InscripcionRequest(10L, 20L, 50L, 40L, " ins-001 ",
                LocalDate.of(2026, 8, 10), inicio, fin, estado, motivo, anteriorId,
                null, version);
    }

    private Inscripcion inscripcion(Long id, EstadoInscripcion estado) {
        Inscripcion entidad = new Inscripcion();
        entidad.setId(id);
        entidad.setVersion(0L);
        entidad.setAlumno(alumno);
        entidad.setPlantel(plantel);
        entidad.setCicloEscolar(ciclo);
        entidad.setGrado(grado);
        entidad.setNumeroInscripcion("INS-ANT");
        entidad.setFechaInscripcion(LocalDate.of(2026, 8, 1));
        entidad.setFechaInicio(LocalDate.of(2026, 8, 20));
        entidad.setEstado(estado);
        return entidad;
    }

    private Grupo grupo(Long id, Integer capacidad) {
        Grupo grupo = new Grupo();
        grupo.setId(id);
        grupo.setPlantel(plantel);
        grupo.setCicloEscolar(ciclo);
        grupo.setGrado(grado);
        grupo.setNombre("A");
        grupo.setTurno(Turno.MATUTINO);
        grupo.setCapacidad(capacidad);
        grupo.setActivo(true);
        return grupo;
    }
}
