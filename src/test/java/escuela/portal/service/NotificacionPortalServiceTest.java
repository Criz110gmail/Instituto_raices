package escuela.portal.service;

import escuela.comunicacion.entity.*;
import escuela.comunicacion.repository.NotificacionUsuarioRepository;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.portal.dto.PortalNotificaciones;
import escuela.portal.repository.PortalNotificacionRepository;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.security.access.AccessDeniedException;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificacionPortalServiceTest {
    private final PortalNotificacionRepository portal = mock(PortalNotificacionRepository.class);
    private final NotificacionUsuarioRepository repository = mock(NotificacionUsuarioRepository.class);
    private final InstitucionService instituciones = mock(InstitucionService.class);
    private final NotificacionPortalService service = new NotificacionPortalService(portal, repository, instituciones);
    private final UsuarioPrincipal principal = new UsuarioPrincipal(7L, 1L, Set.of(), false, false,
            "familia", "x", List.of());

    @BeforeEach
    void preparar() {
        when(instituciones.obtener(1L)).thenReturn(new InstitucionResponse(1L, "RAICES",
                "Instituto Raíces", null, null, null, null, null, null, null,
                null, null, null, "MX", null, "America/Mexico_City", "MXN", true, null));
    }

    @Test
    void sincronizaSinDuplicarEnRepositorioYNormalizaLaPagina() {
        PortalNotificaciones resultadoEsperado = new PortalNotificaciones(2, Page.empty());
        when(portal.consultar(7L, "America/Mexico_City", 0, 10)).thenReturn(resultadoEsperado);

        PortalNotificaciones resultado = service.sincronizarYConsultar(principal, -5);

        assertThat(resultado).isSameAs(resultadoEsperado);
        verify(portal).sincronizar(eq(7L), eq(1L), any(LocalDate.class), any(Instant.class), any(Instant.class));
        verify(portal).consultar(7L, "America/Mexico_City", 0, 10);
    }

    @Test
    void marcaComoLeidaUnaNotificacionPropiaConEventoAccesible() {
        NotificacionUsuario notificacion = evento(7L, 40L);
        when(repository.findByIdForUpdate(90L)).thenReturn(Optional.of(notificacion));
        when(portal.eventoAccesible(eq(7L), eq(1L), eq(40L), any(LocalDate.class))).thenReturn(true);

        String destino = service.marcarLeida(principal, 90L);

        assertThat(destino).isEqualTo("agenda");
        assertThat(notificacion.getLeidaEn()).isNotNull();
        verify(repository).saveAndFlush(notificacion);
    }

    @Test
    void impideLeerUnaNotificacionDeOtraCuenta() {
        NotificacionUsuario notificacion = evento(8L, 40L);
        when(repository.findByIdForUpdate(90L)).thenReturn(Optional.of(notificacion));

        assertThatThrownBy(() -> service.marcarLeida(principal, 90L))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("no pertenece");
        verifyNoInteractions(portal);
        verify(repository, never()).saveAndFlush(any());
    }

    private NotificacionUsuario evento(Long usuarioId, Long eventoId) {
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);
        EventoEscolar evento = new EventoEscolar();
        evento.setId(eventoId);
        NotificacionUsuario notificacion = new NotificacionUsuario();
        notificacion.setId(90L);
        notificacion.setUsuario(usuario);
        notificacion.setTipo(TipoNotificacion.EVENTO);
        notificacion.setEvento(evento);
        return notificacion;
    }
}
