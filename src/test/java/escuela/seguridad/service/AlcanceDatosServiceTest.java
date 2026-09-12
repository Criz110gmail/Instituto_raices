package escuela.seguridad.service;

import escuela.academico.repository.CicloEscolarRepository;
import escuela.academico.repository.GradoRepository;
import escuela.academico.repository.GrupoRepository;
import escuela.academico.repository.NivelEducativoRepository;
import escuela.academico.repository.PeriodoAcademicoRepository;
import escuela.alumno.repository.AlumnoRepository;
import escuela.alumno.entity.Alumno;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.InstitucionRepository;
import escuela.institucion.repository.PlantelNivelRepository;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.repository.RolRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.tutor.repository.TutorRepository;
import escuela.tutor.entity.Tutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AlcanceDatosServiceTest {

    private final PlantelRepository plantelRepository = mock(PlantelRepository.class);
    private final AlumnoRepository alumnoRepository = mock(AlumnoRepository.class);
    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final AlcanceDatosService service = new AlcanceDatosService(
            mock(InstitucionRepository.class), plantelRepository,
            mock(NivelEducativoRepository.class), mock(PlantelNivelRepository.class),
            mock(GradoRepository.class), mock(CicloEscolarRepository.class),
            mock(PeriodoAcademicoRepository.class), mock(GrupoRepository.class),
            alumnoRepository,
            tutorRepository,
            mock(RolRepository.class), mock(UsuarioRepository.class));

    @AfterEach
    void limpiarSesion() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void limitaInstitucionesALaDelUsuario() {
        autenticar(new UsuarioPrincipal(7L, 1L, Set.of(), true, false,
                "admin", "hash", List.of()));
        InstitucionResponse propia = institucion(1L);
        InstitucionResponse ajena = institucion(2L);

        assertThat(service.filtrarInstituciones(List.of(propia, ajena)))
                .containsExactly(propia);
        assertThatThrownBy(() -> service.validarInstitucion(2L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void limitaPlantelesALosAsignados() {
        autenticar(new UsuarioPrincipal(7L, 1L, Set.of(10L), false, false,
                "plantel", "hash", List.of()));
        PlantelResponse permitido = plantel(10L, 1L);

        assertThat(service.filtrarPlanteles(List.of(permitido, plantel(11L, 1L), plantel(20L, 2L))))
                .containsExactly(permitido);
    }

    @Test
    void bloqueaAccesoDirectoAPlantelNoAsignado() {
        autenticar(new UsuarioPrincipal(7L, 1L, Set.of(10L), false, false,
                "plantel", "hash", List.of()));
        Plantel noAsignado = new Plantel();
        noAsignado.setId(11L);
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        noAsignado.setInstitucion(institucion);
        when(plantelRepository.findById(11L)).thenReturn(Optional.of(noAsignado));

        assertThatThrownBy(() -> service.validarPlantel(11L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void bloqueaAccesoDirectoAAlumnoDeOtraInstitucion() {
        autenticar(new UsuarioPrincipal(7L, 1L, Set.of(), true, false,
                "admin", "hash", List.of()));
        Institucion ajena = new Institucion();
        ajena.setId(2L);
        Alumno alumno = new Alumno();
        alumno.setId(30L);
        alumno.setInstitucion(ajena);
        when(alumnoRepository.findById(30L)).thenReturn(Optional.of(alumno));

        assertThatThrownBy(() -> service.validarRecurso(
                escuela.admin.dto.ModuloCatalogo.ALUMNOS, 30L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void bloqueaAccesoDirectoATutorDeOtraInstitucion() {
        autenticar(new UsuarioPrincipal(7L, 1L, Set.of(), true, false,
                "admin", "hash", List.of()));
        Institucion ajena = new Institucion();
        ajena.setId(2L);
        Tutor tutor = new Tutor();
        tutor.setId(40L);
        tutor.setInstitucion(ajena);
        when(tutorRepository.findById(40L)).thenReturn(Optional.of(tutor));

        assertThatThrownBy(() -> service.validarRecurso(
                escuela.admin.dto.ModuloCatalogo.TUTORES, 40L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void recuperacionConservaAccesoGlobal() {
        autenticar(new UsuarioPrincipal(null, null, Set.of(), true, true,
                "recuperacion", "hash", List.of()));

        service.validarNuevaInstitucion();
        assertThat(service.filtrarInstituciones(List.of(institucion(1L), institucion(2L))))
                .hasSize(2);
    }

    @Test
    void vinculosTutorNoConcedenAccesoAdministrativoInstitucional() {
        autenticar(new UsuarioPrincipal(7L, 1L, Set.of(), false, false,
                "tutor", "hash", List.of()));

        assertThat(service.filtrarInstituciones(List.of(institucion(1L)))).isEmpty();
        assertThatThrownBy(() -> service.validarInstitucion(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    private void autenticar(UsuarioPrincipal principal) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
    }

    private InstitucionResponse institucion(Long id) {
        return new InstitucionResponse(id, "I" + id, "Institución " + id, null, null,
                null, null, null, null, null, null, null, null, "MX", null,
                "America/Mexico_City", "MXN", true, null);
    }

    private PlantelResponse plantel(Long id, Long institucionId) {
        return new PlantelResponse(id, institucionId, "P" + id, "Plantel " + id,
                null, null, null, null, null, null, null, null, null, "MX", true, null);
    }
}
