package escuela.config;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class LoginReinicioSeguridadTest {
    @Test void cookieDeAntesDelReinicioPermiteTokenNuevoYUnSoloEnvioEnLosTresPortales() throws Exception {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(Config.class, SeguridadConfig.class, SesionExpiradaAccessDeniedHandler.class,
                    SesionController.class, Paginas.class);
            context.refresh();
            var mvc = MockMvcBuilders.webAppContextSetup(context)
                    .addFilters(context.getBean(FilterChainProxy.class)).build();
            var consultas = context.getBean(AtomicInteger.class);
            var relojCaducado = mvc.perform(get("/sesion/estado").with(LoginReinicioSeguridadTest::cookieVieja)).andReturn();
            assertThat(relojCaducado.getResponse().getStatus()).isEqualTo(401);
            assertThat(relojCaducado.getRequest().getSession(false)).isNull();
            for (String origen : new String[]{"admin", "familias", "maestros"}) {
                String acceso = origen.equals("familias") ? "/familias" : origen.equals("maestros") ? "/maestros/acceso" : "/login";
                for (String ruta : new String[]{acceso, "/js/login-session.js", "/css/admin.css"}) {
                    var resultadoPagina = mvc.perform(get(ruta).with(LoginReinicioSeguridadTest::cookieVieja)).andReturn();
                    var pagina = resultadoPagina.getResponse();
                    assertThat(pagina.getStatus()).isEqualTo(200);
                    assertThat(pagina.getRedirectedUrl()).isNull();
                    assertThat(pagina.getCookie("JSESSIONID")).isNull();
                    assertThat(resultadoPagina.getRequest().getSession(false)).isNull();
                }
                var token = mvc.perform(get("/acceso/csrf").with(LoginReinicioSeguridadTest::cookieVieja)).andReturn();
                assertThat(token.getResponse().getStatus()).isEqualTo(200);
                assertThat(token.getResponse().getHeader("Cache-Control")).isEqualTo("no-store");
                var sesion = (MockHttpSession) token.getRequest().getSession(false);
                var matcher = java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"")
                        .matcher(token.getResponse().getContentAsString());
                assertThat(matcher.find()).isTrue();
                int antes = consultas.get();
                String sesionAntesDeAutenticar = sesion.getId();
                var login = mvc.perform(post("/login").session(sesion).param("_csrf", matcher.group(1))
                        .param("origen", origen).param("username", "usuario-qa").param("password", "clave-qa"))
                        .andReturn().getResponse();
                assertThat(login.getRedirectedUrl()).isEqualTo(origen.equals("admin") ? "/admin" : origen.equals("familias") ? "/portal" : "/maestros");
                assertThat(consultas.get()).isEqualTo(antes + 1);
                assertThat(sesion.getAttribute("SPRING_SECURITY_CONTEXT")).isNotNull();
                assertThat(sesion.getId()).isNotEqualTo(sesionAntesDeAutenticar);
                var tokenSalida = mvc.perform(get("/acceso/csrf").session(sesion)).andReturn();
                var salidaMatcher = java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"")
                        .matcher(tokenSalida.getResponse().getContentAsString());
                assertThat(salidaMatcher.find()).isTrue();
                var salida = mvc.perform(post("/logout").session(sesion).param("_csrf", salidaMatcher.group(1))
                        .param("origen", origen)).andReturn().getResponse();
                assertThat(salida.getRedirectedUrl()).isEqualTo(acceso + "?logout");
                assertThat(salida.getCookie("JSESSIONID").getMaxAge()).isZero();
                assertThat(sesion.isInvalid()).isTrue();
                var privado = mvc.perform(get("/admin").with(LoginReinicioSeguridadTest::cookieVieja)).andReturn().getResponse();
                assertThat(privado.getRedirectedUrl()).isEqualTo("/login?sesionExpirada");
                assertThat(privado.getCookie("JSESSIONID")).isNull();
                var sinCsrf = mvc.perform(post("/login").param("username", "usuario-qa").param("password", "clave-qa"))
                        .andReturn().getResponse();
                assertThat(sinCsrf.getRedirectedUrl()).isEqualTo("/login?accesoActualizado");
                assertThat(consultas.get()).isEqualTo(antes + 1);
            }
        }
    }

    private static org.springframework.mock.web.MockHttpServletRequest cookieVieja(
            org.springframework.mock.web.MockHttpServletRequest request) {
        request.setRequestedSessionId("antes-del-reinicio");
        request.setRequestedSessionIdValid(false);
        return request;
    }

    @Configuration @EnableWebSecurity @EnableWebMvc
    static class Config {
        @Bean AtomicInteger consultas() { return new AtomicInteger(); }
        @Bean UserDetailsService usuarios(AtomicInteger consultas) {
            var usuarios = new InMemoryUserDetailsManager(User.withUsername("usuario-qa")
                    .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("clave-qa"))
                    .authorities("PORTAL_TUTOR_ACCEDER", "PORTAL_MAESTRO_ACCEDER").build());
            return username -> { consultas.incrementAndGet(); return usuarios.loadUserByUsername(username); };
        }
    }
    @RestController static class Paginas {
        @GetMapping({"/login", "/familias", "/maestros/acceso", "/js/login-session.js", "/css/admin.css"})
        String publica() { return "acceso público de prueba"; }
    }
}
