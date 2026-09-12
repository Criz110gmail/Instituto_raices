package escuela.seguridad.service.impl;

import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.dto.request.ActivacionUsuarioRequest;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.InvitacionUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.InvitacionUsuarioRepository;
import escuela.seguridad.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvitacionUsuarioServiceImplTest {

    private final InvitacionUsuarioRepository invitacionRepository = mock(InvitacionUsuarioRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private InvitacionUsuarioServiceImpl service;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEstado(EstadoUsuario.INVITADO);
        when(usuarioRepository.buscarPorIdConBloqueo(10L)).thenReturn(Optional.of(usuario));
        when(invitacionRepository.findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(10L))
                .thenReturn(List.of());
        when(invitacionRepository.saveAndFlush(any(InvitacionUsuario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        service = new InvitacionUsuarioServiceImpl(invitacionRepository, usuarioRepository, passwordEncoder);
    }

    @Test
    void emiteTokenCrudoPeroPersisteSoloSuHash() {
        var emitida = service.emitir(10L, Duration.ofHours(24));

        ArgumentCaptor<InvitacionUsuario> captor = ArgumentCaptor.forClass(InvitacionUsuario.class);
        verify(invitacionRepository).saveAndFlush(captor.capture());
        assertThat(emitida.token()).hasSize(43);
        assertThat(captor.getValue().getTokenHash())
                .hasSize(64)
                .doesNotContain(emitida.token());
        assertThat(captor.getValue().getExpiraEn()).isAfter(Instant.now());
    }

    @Test
    void activaUsuarioConPasswordCifradoYConsumeInvitacion() {
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setUsuario(usuario);
        invitacion.setExpiraEn(Instant.now().plusSeconds(3600));
        when(invitacionRepository.buscarPorTokenHashConBloqueo(anyString()))
                .thenReturn(Optional.of(invitacion));
        when(passwordEncoder.encode("una-clave-muy-segura"))
                .thenReturn("hash-bcrypt");

        service.activar(new ActivacionUsuarioRequest("token-valido", "una-clave-muy-segura"));

        assertThat(usuario.getPasswordHash()).isEqualTo("hash-bcrypt");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(invitacion.getUsadoEn()).isNotNull();
    }

    @Test
    void rechazaReutilizarUnaInvitacion() {
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setUsuario(usuario);
        invitacion.setExpiraEn(Instant.now().plusSeconds(3600));
        invitacion.setUsadoEn(Instant.now());
        when(invitacionRepository.buscarPorTokenHashConBloqueo(anyString()))
                .thenReturn(Optional.of(invitacion));

        assertThatThrownBy(() -> service.activar(
                new ActivacionUsuarioRequest("token-usado", "una-clave-muy-segura")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya fue utilizada");
    }
}
