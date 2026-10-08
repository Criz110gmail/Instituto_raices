package escuela.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class SesionActividadFilterTest {
    private final long ahora = 2000000;
    private final SesionActividadFilter filter = new SesionActividadFilter(new SesionExpiradaAccessDeniedHandler(),
            Clock.fixed(Instant.ofEpochMilli(ahora), ZoneOffset.UTC));
    @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }
    private MockHttpServletRequest request(String ruta, long ultima) {
        var request = new MockHttpServletRequest("GET", ruta);
        var session = new MockHttpSession();
        session.setMaxInactiveInterval(1800);
        session.setAttribute(SesionActividadFilter.ACTIVIDAD, ultima);
        request.setSession(session);
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("usuario", "", List.of()));
        return request;
    }
    @Test void relojNoRenuevaAunqueElContenedorActualiceSuUltimoAcceso() throws Exception {
        var request = request("/sesion/estado", ahora - 1700000);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req,res) -> {});
        assertThat(request.getSession().getAttribute(SesionActividadFilter.ACTIVIDAD)).isEqualTo(ahora - 1700000);
        assertThat(response.getHeader("X-Session-Expires")).isEqualTo(Long.toString(ahora + 100000));
    }
    @Test void peticionNormalRenuevaTiempo() throws Exception {
        var request = request("/portal/pagos", ahora - 1700000);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req,res) -> {});
        assertThat(request.getSession().getAttribute(SesionActividadFilter.ACTIVIDAD)).isEqualTo(ahora);
        assertThat(response.getHeader("X-Session-Expires")).isEqualTo(Long.toString(ahora + 1800000));
    }
    @Test void archivosEstaticosNoRenuevanTiempo() throws Exception {
        var request = request("/js/theme.js", ahora - 100000);
        filter.doFilter(request, new MockHttpServletResponse(), (req,res) -> {});
        assertThat(request.getSession().getAttribute(SesionActividadFilter.ACTIVIDAD)).isEqualTo(ahora - 100000);
    }
    @Test void consultaDeSesionCaducadaNoPuedeResucitarla() throws Exception {
        for (String ruta : List.of("/sesion/estado", "/sesion/renovar")) {
            var request = request(ruta, ahora - 1800000);
            var session = (MockHttpSession) request.getSession();
            var response = new MockHttpServletResponse();
            filter.doFilter(request, response, (req,res) -> { throw new AssertionError("No debe llegar al controlador"); });
            assertThat(response.getStatus()).isEqualTo(401);
            assertThat(session.isInvalid()).isTrue();
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }
    @Test void vencimientoLogicoRedirigeAlPortalPropio() throws Exception {
        var request = request("/maestros/planeaciones", ahora - 1800000);
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req,res) -> { throw new AssertionError(); });
        assertThat(response.getRedirectedUrl()).isEqualTo("/maestros/acceso?sesionExpirada");
    }
    @Test void sinSesionNoCreaUnaNueva() throws Exception {
        var request = new MockHttpServletRequest("GET", "/sesion/estado");
        filter.doFilter(request, new MockHttpServletResponse(), (req,res) -> {});
        assertThat(request.getSession(false)).isNull();
    }
}
