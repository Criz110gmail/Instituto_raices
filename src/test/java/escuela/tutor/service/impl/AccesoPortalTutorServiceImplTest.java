package escuela.tutor.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.entity.Institucion;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.TipoCuentaUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.tutor.dto.request.PortalTutorCuentaRequest;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccesoPortalTutorServiceImplTest {

    private final TutorRepository tutorRepository = mock(TutorRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final AccesoPortalTutorServiceImpl service =
            new AccesoPortalTutorServiceImpl(tutorRepository, usuarioRepository);
    private Tutor tutor;

    @BeforeEach
    void preparar() {
        Institucion institucion = new Institucion();
        institucion.setId(1L);
        tutor = new Tutor();
        tutor.setId(10L);
        tutor.setInstitucion(institucion);
        tutor.setNombres("María Fernanda");
        tutor.setPrimerApellido("López");
        tutor.setActivo(true);
        when(tutorRepository.findById(10L)).thenReturn(Optional.of(tutor));
        when(tutorRepository.findByIdForUpdate(10L)).thenReturn(Optional.of(tutor));
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario usuario = invocacion.getArgument(0);
            usuario.setId(20L);
            return usuario;
        });
        when(tutorRepository.saveAndFlush(any(Tutor.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void sugiereNombreNormalizadoYDisponible() {
        assertThat(service.sugerirUsername(10L)).isEqualTo("maria.lopez");
    }

    @Test
    void agregaSufijoCuandoLaPrimeraSugerenciaYaExiste() {
        when(usuarioRepository.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(
                1L, "maria.lopez", 0L)).thenReturn(true);

        assertThat(service.sugerirUsername(10L)).isEqualTo("maria.lopez2");
    }

    @Test
    void creaCuentaExclusivaDelPortalYLaVincula() {
        var respuesta = service.crear(10L,
                new PortalTutorCuentaRequest("maria.lopez", "Maria@Correo.MX"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getTipoCuenta()).isEqualTo(TipoCuentaUsuario.PORTAL_TUTOR);
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoUsuario.INVITADO);
        assertThat(captor.getValue().getEmail()).isEqualTo("maria@correo.mx");
        assertThat(tutor.getUsuario()).isSameAs(captor.getValue());
        assertThat(respuesta.usuarioId()).isEqualTo(20L);
    }

    @Test
    void noCreaSegundaCuentaParaElMismoTutor() {
        tutor.setUsuario(cuenta(EstadoUsuario.ACTIVO, true));

        assertThatThrownBy(() -> service.crear(10L,
                new PortalTutorCuentaRequest("otra", "otra@correo.mx")))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("ya tiene una cuenta");
    }

    @Test
    void desactivaYReactivaConservandoCredencial() {
        Usuario usuario = cuenta(EstadoUsuario.ACTIVO, true);
        tutor.setUsuario(usuario);

        service.cambiarDisponibilidad(10L, 3L, false);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INACTIVO);

        usuario.setVersion(4L);
        service.cambiarDisponibilidad(10L, 4L, true);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void reactivarSinPasswordRegresaAPendienteDeActivacion() {
        Usuario usuario = cuenta(EstadoUsuario.INACTIVO, false);
        tutor.setUsuario(usuario);

        service.cambiarDisponibilidad(10L, 3L, true);

        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.INVITADO);
    }

    private Usuario cuenta(EstadoUsuario estado, boolean password) {
        Usuario usuario = new Usuario();
        usuario.setId(20L);
        usuario.setVersion(3L);
        usuario.setInstitucion(tutor.getInstitucion());
        usuario.setUsername("maria.lopez");
        usuario.setEmail("maria@correo.mx");
        usuario.setTipoCuenta(TipoCuentaUsuario.PORTAL_TUTOR);
        usuario.setEstado(estado);
        if (password) usuario.setPasswordHash("hash");
        return usuario;
    }
}
