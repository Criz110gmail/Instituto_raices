package escuela.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class SesionControllerTest {
    private final SesionController controller = new SesionController();
    @AfterEach void limpiar() { SecurityContextHolder.clearContext(); }
    private MockHttpServletRequest request(long ultima) {
        var request = new MockHttpServletRequest();
        var session = new MockHttpSession();
        session.setMaxInactiveInterval(1800);
        session.setAttribute(SesionActividadFilter.ACTIVIDAD, ultima);
        request.setSession(session);
        request.setAttribute(CsrfToken.class.getName(), new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-prueba"));
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated("usuario", "", List.of()));
        return request;
    }
    @Test void consultaNoRenuevaYEntregaRelojServidorYCsrf() {
        long ultima = System.currentTimeMillis() - 1000000;
        var request = request(ultima);
        var respuesta = controller.estado(request);
        assertThat(respuesta.getBody().vence()).isEqualTo(ultima + 1800000);
        assertThat(respuesta.getBody().csrfToken()).isEqualTo("token-prueba");
        assertThat(request.getSession().getAttribute(SesionActividadFilter.ACTIVIDAD)).isEqualTo(ultima);
        assertThat(respuesta.getHeaders().getFirst("Cache-Control")).isEqualTo("no-store");
    }
    @Test void confirmarRenuevaOtrosTreintaMinutos() {
        var request = request(System.currentTimeMillis() - 1700000);
        var respuesta = controller.renovar(request);
        assertThat(respuesta.getBody().vence() - respuesta.getBody().ahora()).isEqualTo(1800000);
        assertThat(request.getSession().getAttribute(SesionActividadFilter.ACTIVIDAD)).isEqualTo(respuesta.getBody().ahora());
    }
    @Test void sesionCaducadaNoPuedeRenovarse() {
        var request = request(System.currentTimeMillis() - 1800001);
        var session = (MockHttpSession) request.getSession();
        assertThat(controller.renovar(request).getStatusCode().value()).isEqualTo(401);
        assertThat(session.isInvalid()).isTrue();
    }
    @Test void sinAutenticacionNoCreaSesion() {
        var request = new MockHttpServletRequest();
        assertThat(controller.estado(request).getStatusCode().value()).isEqualTo(401);
        assertThat(controller.renovar(request).getStatusCode().value()).isEqualTo(401);
        assertThat(request.getSession(false)).isNull();
    }
}
