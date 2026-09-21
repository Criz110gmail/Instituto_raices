package escuela.seguridad.service.impl;

import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.entity.Plantel;
import escuela.institucion.repository.PlantelRepository;
import escuela.seguridad.dto.request.AsignacionRolRequest;
import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.Rol;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.entity.UsuarioRol;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.repository.RolRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.repository.UsuarioRolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdministracionAccesoServiceImplTest {

    private final RolRepository rolRepository = mock(RolRepository.class);
    private final PermisoRepository permisoRepository = mock(PermisoRepository.class);
    private final RolPermisoRepository rolPermisoRepository = mock(RolPermisoRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final UsuarioRolRepository usuarioRolRepository = mock(UsuarioRolRepository.class);
    private final PlantelRepository plantelRepository = mock(PlantelRepository.class);
    private final RegistroAuditoriaService auditoria = mock(RegistroAuditoriaService.class);
    private AdministracionAccesoServiceImpl service;
    private Usuario usuario;
    private Rol rol;

    @BeforeEach
    void preparar() {
        Institucion institucion = institucion(1L);
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setInstitucion(institucion);
        rol = new Rol();
        rol.setId(20L);
        rol.setInstitucion(institucion);
        rol.setActivo(true);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));
        when(rolRepository.findById(20L)).thenReturn(Optional.of(rol));
        service = new AdministracionAccesoServiceImpl(rolRepository, permisoRepository,
                rolPermisoRepository, usuarioRepository, usuarioRolRepository, plantelRepository, auditoria);
    }

    @Test
    void rechazaPlantelDeOtraInstitucion() {
        Plantel plantel = new Plantel();
        plantel.setId(30L);
        plantel.setInstitucion(institucion(2L));
        plantel.setActivo(true);
        when(plantelRepository.findById(30L)).thenReturn(Optional.of(plantel));

        AsignacionRolRequest request = new AsignacionRolRequest(
                10L, 20L, AlcanceRol.PLANTEL, 30L);

        assertThatThrownBy(() -> service.asignarRol(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("institución del usuario");
    }

    @Test
    void exigePlantelCuandoElAlcanceEsPlantel() {
        AsignacionRolRequest request = new AsignacionRolRequest(
                10L, 20L, AlcanceRol.PLANTEL, null);

        assertThatThrownBy(() -> service.asignarRol(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("obligatorio");
    }

    @Test
    void rechazaPlantelEnAlcanceInstitucional() {
        AsignacionRolRequest request = new AsignacionRolRequest(
                10L, 20L, AlcanceRol.INSTITUCION, 30L);

        assertThatThrownBy(() -> service.asignarRol(request))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("solo se permite");
    }

    @Test
    void rechazaDesactivarAsignacionDeOtroUsuario() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(99L);
        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setId(40L);
        asignacion.setVersion(1L);
        asignacion.setUsuario(otroUsuario);
        when(usuarioRolRepository.findById(40L)).thenReturn(Optional.of(asignacion));

        assertThatThrownBy(() -> service.desactivarAsignacion(10L, 40L, 1L))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no pertenece");
    }

    private Institucion institucion(Long id) {
        Institucion institucion = new Institucion();
        institucion.setId(id);
        institucion.setActivo(true);
        return institucion;
    }
}
