package escuela.seguridad.service.impl;

import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.dto.request.RestablecimientoPasswordRequest;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.RecuperacionPassword;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.RecuperacionPasswordRepository;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecuperacionPasswordServiceImplTest {

    private final RecuperacionPasswordRepository recuperacionRepository =
            mock(RecuperacionPasswordRepository.class);
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private RecuperacionPasswordServiceImpl service;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        usuario = new Usuario();
        usuario.setId(10L);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setPasswordHash("hash-anterior");
        when(usuarioRepository.buscarPorIdConBloqueo(10L)).thenReturn(Optional.of(usuario));
        when(recuperacionRepository.findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(10L))
                .thenReturn(List.of());
        when(recuperacionRepository.saveAndFlush(any(RecuperacionPassword.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        service = new RecuperacionPasswordServiceImpl(
                recuperacionRepository, usuarioRepository, passwordEncoder);
    }

    @Test
    void emiteTokenCrudoPeroPersisteSoloSuHash() {
        var emitida = service.emitir(10L, Duration.ofMinutes(30));

        ArgumentCaptor<RecuperacionPassword> captor =
                ArgumentCaptor.forClass(RecuperacionPassword.class);
        verify(recuperacionRepository).saveAndFlush(captor.capture());
        assertThat(emitida.token()).hasSize(43);
        assertThat(captor.getValue().getTokenHash())
                .hasSize(64)
                .doesNotContain(emitida.token());
        assertThat(captor.getValue().getExpiraEn()).isAfter(Instant.now());
    }

    @Test
    void revocaElEnlaceAnteriorAlEmitirOtro() {
        RecuperacionPassword anterior = new RecuperacionPassword();
        when(recuperacionRepository.findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(10L))
                .thenReturn(List.of(anterior));

        service.emitir(10L, Duration.ofMinutes(30));

        assertThat(anterior.getRevocadoEn()).isNotNull();
        verify(recuperacionRepository).saveAllAndFlush(List.of(anterior));
    }

    @Test
    void restableceConBcryptConsumeTokenYLimpiaBloqueoTemporal() {
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuario.setBloqueoHasta(Instant.now().plusSeconds(600));
        usuario.setIntentosFallidos(5);
        RecuperacionPassword recuperacion = recuperacionVigente();
        when(recuperacionRepository.buscarPorTokenHashConBloqueo(anyString()))
                .thenReturn(Optional.of(recuperacion));
        when(passwordEncoder.encode("una-clave-muy-segura")).thenReturn("nuevo-hash-bcrypt");

        service.restablecer(new RestablecimientoPasswordRequest(
                "token-valido", "una-clave-muy-segura"));

        assertThat(usuario.getPasswordHash()).isEqualTo("nuevo-hash-bcrypt");
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(usuario.getBloqueoHasta()).isNull();
        assertThat(usuario.getIntentosFallidos()).isZero();
        assertThat(recuperacion.getUsadoEn()).isNotNull();
    }

    @Test
    void rechazaReutilizarElToken() {
        RecuperacionPassword recuperacion = recuperacionVigente();
        recuperacion.setUsadoEn(Instant.now());
        when(recuperacionRepository.buscarPorTokenHashConBloqueo(anyString()))
                .thenReturn(Optional.of(recuperacion));

        assertThatThrownBy(() -> service.restablecer(new RestablecimientoPasswordRequest(
                "token-usado", "una-clave-muy-segura")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya fue utilizado");
    }

    @Test
    void noPermiteQueRecuperacionQuiteUnBloqueoAdministrativo() {
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuario.setBloqueoHasta(null);
        RecuperacionPassword recuperacion = recuperacionVigente();
        when(recuperacionRepository.buscarPorTokenHashConBloqueo(anyString()))
                .thenReturn(Optional.of(recuperacion));

        assertThatThrownBy(() -> service.restablecer(new RestablecimientoPasswordRequest(
                "token-valido", "una-clave-muy-segura")))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya no está disponible");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void noEmiteRecuperacionParaInvitados() {
        usuario.setEstado(EstadoUsuario.INVITADO);
        usuario.setPasswordHash(null);

        assertThatThrownBy(() -> service.emitir(10L, Duration.ofMinutes(30)))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("usuario activo");
    }

    private RecuperacionPassword recuperacionVigente() {
        RecuperacionPassword recuperacion = new RecuperacionPassword();
        recuperacion.setUsuario(usuario);
        recuperacion.setExpiraEn(Instant.now().plusSeconds(1800));
        return recuperacion;
    }
}
