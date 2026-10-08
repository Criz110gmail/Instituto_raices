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
    void conservaElAccesoDocenteCuandoCaducaLaSesion() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/maestros/planeaciones");
        request.setRequestedSessionId("sesion-caducada");
        request.setRequestedSessionIdValid(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        var tokenEsperado = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-nuevo");
        handler.handle(request, response,
                new InvalidCsrfTokenException(tokenEsperado, "token-anterior"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/maestros/acceso?sesionExpirada");
        assertThat(response.getCookie("JSESSIONID")).isNotNull();
        assertThat(response.getCookie("JSESSIONID").getMaxAge()).isZero();
    }

    @Test
    void conservaElAccesoFamiliarCuandoCaducaLaSesion() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/portal/notificaciones/1/leida");
        request.setRequestedSessionId("sesion-caducada");
        request.setRequestedSessionIdValid(false);
        MockHttpServletResponse response = new MockHttpServletResponse();

        var tokenEsperado = new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", "token-nuevo");
        handler.handle(request, response,
                new InvalidCsrfTokenException(tokenEsperado, "token-anterior"));

        assertThat(response.getRedirectedUrl()).isEqualTo("/familias?sesionExpirada");
    }

    @Test
    void conservaContextoYBorraCookieSeguraEnConsultaFamiliarCaducada() throws Exception {
        var request = new MockHttpServletRequest("GET", "/nexo/portal/pagos");
        request.setContextPath("/nexo");
        request.setSecure(true);
        var response = new MockHttpServletResponse();
        handler.redirigir(request, response);
        assertThat(response.getRedirectedUrl()).isEqualTo("/nexo/familias?sesionExpirada");
        assertThat(response.getCookie("JSESSIONID").getPath()).isEqualTo("/nexo");
        assertThat(response.getCookie("JSESSIONID").getSecure()).isTrue();
        assertThat(response.getCookie("JSESSIONID").isHttpOnly()).isTrue();
    }

    @Test
    void reconoceOrigenDeLogoutAunqueLaSesionYaHayaCaducado() throws Exception {
        for (String origen : new String[]{"familias", "maestros", "desconocido"}) {
            var request = new MockHttpServletRequest("POST", "/logout");
            request.setParameter("origen", origen);
            var response = new MockHttpServletResponse();
            handler.redirigir(request, response);
            String login = origen.equals("familias") ? "/familias" : origen.equals("maestros") ? "/maestros/acceso" : "/login";
            assertThat(response.getRedirectedUrl()).isEqualTo(login + "?sesionExpirada");
        }
    }

    @Test
    void noPermiteQueParametroOrigenCambieElPortalDeUnaRutaAdministrativa() throws Exception {
        var request = new MockHttpServletRequest("GET", "/admin/pagos");
        request.setParameter("origen", "familias");
        var response = new MockHttpServletResponse();
        handler.redirigir(request, response);
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
