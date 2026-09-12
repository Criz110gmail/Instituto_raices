package escuela.seguridad.service.impl;

import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.security.autoconfigure.SecurityProperties;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegistroAutenticacionServiceImplTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private RegistroAutenticacionServiceImpl service;
    private Usuario usuario;

    @BeforeEach
    void preparar() {
        SecurityProperties properties = new SecurityProperties();
        properties.getUser().setName("recuperacion");
        service = new RegistroAutenticacionServiceImpl(repository, properties);
        usuario = new Usuario();
        usuario.setId(7L);
        usuario.setUsername("criz110");
        usuario.setPasswordHash("hash");
        usuario.setEstado(EstadoUsuario.ACTIVO);
    }

    @Test
    void accesoCorrectoRegistraFechaYReiniciaFallos() {
        usuario.setIntentosFallidos(3);
        usuario.setBloqueoHasta(Instant.now().plusSeconds(30));
        when(repository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(usuario));

        service.registrarExito(principal(7L, false));

        assertThat(usuario.getUltimoAccesoEn()).isNotNull();
        assertThat(usuario.getIntentosFallidos()).isZero();
        assertThat(usuario.getBloqueoHasta()).isNull();
        verify(repository).saveAndFlush(usuario);
    }

    @Test
    void quintoFalloBloqueaDuranteQuinceMinutos() {
        usuario.setIntentosFallidos(4);
        when(repository.findAllByUsernameIgnoreCase("criz110")).thenReturn(List.of(usuario));
        when(repository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(usuario));
        Instant antes = Instant.now().plusSeconds(14 * 60);

        service.registrarFallo("criz110");

        assertThat(usuario.getIntentosFallidos()).isEqualTo(5);
        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.BLOQUEADO);
        assertThat(usuario.getBloqueoHasta()).isAfter(antes);
        verify(repository).saveAndFlush(usuario);
    }

    @Test
    void nuevoFalloDespuesDeVencerReiniciaLaVentana() {
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuario.setIntentosFallidos(5);
        usuario.setBloqueoHasta(Instant.now().minusSeconds(1));
        when(repository.findByInstitucionCodigoIgnoreCaseAndUsernameIgnoreCase("RAICES", "criz110"))
                .thenReturn(Optional.of(usuario));
        when(repository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(usuario));

        service.registrarFallo("RAICES\\criz110");

        assertThat(usuario.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
        assertThat(usuario.getIntentosFallidos()).isEqualTo(1);
        assertThat(usuario.getBloqueoHasta()).isNull();
    }

    @Test
    void noRegistraFallosParaUsuarioDesconocidoOAcessoRecuperacion() {
        when(repository.findAllByUsernameIgnoreCase("desconocido")).thenReturn(List.of());

        service.registrarFallo("desconocido");
        service.registrarFallo("recuperacion");
        service.registrarExito(principal(null, true));

        verify(repository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void bloqueoAdministrativoNoSeExtiendeConNuevosIntentos() {
        usuario.setEstado(EstadoUsuario.BLOQUEADO);
        usuario.setBloqueoHasta(null);
        when(repository.findAllByUsernameIgnoreCase("criz110")).thenReturn(List.of(usuario));
        when(repository.buscarPorIdConBloqueo(7L)).thenReturn(Optional.of(usuario));

        service.registrarFallo("criz110");

        verify(repository, never()).saveAndFlush(usuario);
    }

    private UsuarioPrincipal principal(Long id, boolean recuperacion) {
        return new UsuarioPrincipal(id, recuperacion ? null : 1L, Set.of(), true,
                recuperacion, recuperacion ? "recuperacion" : "criz110", "hash", List.of());
    }
}
