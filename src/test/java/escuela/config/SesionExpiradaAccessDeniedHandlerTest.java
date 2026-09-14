package escuela.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;

import static org.assertj.core.api.Assertions.assertThat;

class SesionExpiradaAccessDeniedHandlerTest {

    private final SesionExpiradaAccessDeniedHandler handler =
            new SesionExpiradaAccessDeniedHandler();

    @Test
    void redirigeAlLoginCuandoElCsrfPerteneceAUnaSesionCaducada() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/admin/grupos");
        request.setRequestedSessionId("sesion-caducada");
        request.setRequestedSessionIdValid(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        var tokenEsperado = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-nuevo");
        handler.handle(request, response,
                new InvalidCsrfTokenException(tokenEsperado, "token-anterior"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/login?sesionExpirada");
    }

    @Test
    void conservaElAccesoDenegadoParaErroresDePermisos() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/admin/usuarios");
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(request, response, new AccessDeniedException("Sin permiso"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getForwardedUrl()).isEqualTo("/acceso-denegado");
    }
}
