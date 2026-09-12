package escuela.seguridad.service.impl;

import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Permiso;
import escuela.seguridad.entity.Rol;
import escuela.seguridad.entity.RolPermiso;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.entity.UsuarioRol;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.repository.UsuarioRolRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UsuarioSistemaDetailsServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final UsuarioRolRepository usuarioRolRepository = mock(UsuarioRolRepository.class);
    private final RolPermisoRepository rolPermisoRepository = mock(RolPermisoRepository.class);
    private final PermisoRepository permisoRepository = mock(PermisoRepository.class);
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private UsuarioSistemaDetailsService service;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        SecurityProperties properties = new SecurityProperties();
        properties.getUser().setName("administrador-temporal");
        properties.getUser().setPassword("password-temporal");
        service = new UsuarioSistemaDetailsService(usuarioRepository, usuarioRolRepository,
                rolPermisoRepository, permisoRepository, properties, passwordEncoder);

        Institucion institucion = new Institucion();
        institucion.setId(1L);
        institucion.setCodigo("INSTITUTO RAICES");

        usuario = new Usuario();
        usuario.setId(2L);
        usuario.setInstitucion(institucion);
        usuario.setUsername("criz110");
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setPasswordHash(passwordEncoder.encode("password-elegida"));
    }

    @Test
    void autenticaUsuarioActivoConSuHashYPermisosVigentes() {
        Rol rol = new Rol();
        rol.setId(3L);
        rol.setActivo(true);
        UsuarioRol asignacion = new UsuarioRol();
        asignacion.setRol(rol);

        Permiso permiso = new Permiso();
        permiso.setCodigo("USUARIO_ADMINISTRAR");
        RolPermiso relacion = new RolPermiso();
        relacion.setPermiso(permiso);

        when(usuarioRepository.findAllByUsernameIgnoreCase("criz110")).thenReturn(List.of(usuario));
        when(usuarioRolRepository.findAllByUsuarioIdAndActivoTrueOrderByRolNombreAsc(2L))
                .thenReturn(List.of(asignacion));
        when(rolPermisoRepository.findAllByRolIdAndActivoTrueOrderByPermisoCodigoAsc(3L))
                .thenReturn(List.of(relacion));

        var detalles = service.loadUserByUsername("criz110");

        assertThat(detalles.getUsername()).isEqualTo("criz110");
        assertThat(passwordEncoder.matches("password-elegida", detalles.getPassword())).isTrue();
        assertThat(detalles.getAuthorities()).extracting("authority")
                .containsExactly("USUARIO_ADMINISTRAR");
    }

    @Test
    void exigeCodigoDeInstitucionCuandoElUsernameEstaDuplicado() {
        when(usuarioRepository.findAllByUsernameIgnoreCase("criz110"))
                .thenReturn(List.of(usuario, new Usuario()));
        when(usuarioRepository.findByInstitucionCodigoIgnoreCaseAndUsernameIgnoreCase(
                "INSTITUTO RAICES", "criz110")).thenReturn(Optional.of(usuario));
        when(usuarioRolRepository.findAllByUsuarioIdAndActivoTrueOrderByRolNombreAsc(2L))
                .thenReturn(List.of());

        assertThatThrownBy(() -> service.loadUserByUsername("criz110"))
                .isInstanceOf(UsernameNotFoundException.class);
        assertThat(service.loadUserByUsername("INSTITUTO RAICES\\criz110").getUsername())
                .isEqualTo("criz110");
    }

    @Test
    void conservaElUsuarioTemporalComoAccesoDeRecuperacion() {
        Permiso permiso = new Permiso();
        permiso.setCodigo("USUARIO_ADMINISTRAR");
        when(permisoRepository.findAllByOrderByCodigoAsc()).thenReturn(List.of(permiso));

        var detalles = service.loadUserByUsername("administrador-temporal");

        assertThat(passwordEncoder.matches("password-temporal", detalles.getPassword())).isTrue();
        assertThat(detalles.getAuthorities()).extracting("authority")
                .containsExactly("USUARIO_ADMINISTRAR");
    }
}
