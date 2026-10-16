package escuela.config;

import escuela.inicio.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class LoginRenderSinSesionTest {
    @Test void formularioPrivadoQueTerminaDespuesDeLogoutNoRecreaSesion() throws Exception {
        try(var context=new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,
                    SesionExpiradaAccessDeniedHandler.class,SesionController.class,LoginController.class,Vistas.class,SalidaDuranteRender.class);
            context.refresh();
            var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(context.getBean(FilterChainProxy.class)).build();
            var token=mvc.perform(get("/acceso/csrf")).andReturn();
            var sesion=(MockHttpSession)token.getRequest().getSession(false);
            var matcher=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(token.getResponse().getContentAsString());
            assertThat(matcher.find()).isTrue();
            mvc.perform(post("/login").session(sesion).param("_csrf",matcher.group(1))
                    .param("username","usuario-qa").param("password","clave-qa"));
            var atrasada=mvc.perform(get("/admin/qa-salida-atrasada").session(sesion)).andReturn();
            assertThat(atrasada.getResponse().getStatus()).isEqualTo(200);
            assertThat(atrasada.getRequest().getSession(false)).as("Una vista privada atrasada no puede crear otra cookie después de logout").isNull();
            // El siguiente acceso usa un token fresco una sola vez, sin reenviar credenciales.
            var siguiente=mvc.perform(get("/acceso/csrf")).andReturn();
            var nueva=(MockHttpSession)siguiente.getRequest().getSession(false);
            var actual=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(siguiente.getResponse().getContentAsString());
            assertThat(actual.find()).isTrue();
            var entrada=mvc.perform(post("/login").session(nueva).param("_csrf",actual.group(1))
                    .param("username","usuario-qa").param("password","clave-qa")).andReturn();
            assertThat(entrada.getResponse().getRedirectedUrl()).isEqualTo("/admin");
            assertThat(nueva.getAttribute("SPRING_SECURITY_CONTEXT")).isNotNull();
        }
    }
    @org.springframework.stereotype.Controller static class SalidaDuranteRender {
        @org.springframework.web.bind.annotation.GetMapping("/admin/qa-salida-atrasada")
        String mostrar(jakarta.servlet.http.HttpServletRequest request) {
            // Simula logout en otra solicitud después de autorizar, antes de renderizar CSRF.
            request.getSession(false).invalidate();
            return "qa/salida-atrasada";
        }
    }
    @Test void paginasDeAccesoRealesNoCreanSesionNiPisanElTokenDelPrimerEnvio() throws Exception {
        try(var context=new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,
                    SesionExpiradaAccessDeniedHandler.class,SesionController.class,LoginController.class,Vistas.class);
            context.refresh();
            var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(context.getBean(FilterChainProxy.class)).build();
            for(String origen:new String[]{"admin","familias","maestros"}) {
                String acceso=origen.equals("admin")?"/login":origen.equals("familias")?"/familias":"/maestros/acceso";
                var pagina=mvc.perform(get(acceso).with(r->{r.setRequestedSessionId("qa-anterior");r.setRequestedSessionIdValid(false);return r;})).andReturn();
                assertThat(pagina.getResponse().getStatus()).isEqualTo(200);
                assertThat(pagina.getRequest().getSession(false)).isNull();
                assertThat(pagina.getResponse().getContentAsString()).contains("action=\"/login\"","name=\"_csrf\" value=\"\"","Activa JavaScript","type=\"submit\" disabled");
                var token=mvc.perform(get("/acceso/csrf")).andReturn();
                var sesion=(MockHttpSession)token.getRequest().getSession(false);
                var matcher=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(token.getResponse().getContentAsString());
                assertThat(matcher.find()).isTrue();
                // Cargar cualquier otro acceso con una cookie vieja no debe emitir otra sesión.
                for(String atrasada:new String[]{"/login?sesionExpirada","/familias","/maestros/acceso","/login?origen=familias","/login?origen=maestros"}) {
                    var otro=mvc.perform(get(atrasada).with(r->{r.setRequestedSessionId("qa-anterior");r.setRequestedSessionIdValid(false);return r;})).andReturn();
                    assertThat(otro.getResponse().getStatus()).isEqualTo(200);
                    assertThat(otro.getRequest().getSession(false)).isNull();
                    assertThat(otro.getResponse().getCookie("JSESSIONID")).isNull();
                }
                var respuesta=mvc.perform(post("/login").session(sesion).param("_csrf",matcher.group(1)).param("origen",origen)
                        .param("username","usuario-qa").param("password","clave-qa")).andReturn().getResponse();
                assertThat(respuesta.getRedirectedUrl()).isEqualTo(origen.equals("admin")?"/admin":origen.equals("familias")?"/portal":"/maestros");
                assertThat(sesion.getAttribute("SPRING_SECURITY_CONTEXT")).isNotNull();
            }
        }
    }
    @Configuration static class Vistas {
        @Bean ThymeleafViewResolver vistas() {
            var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
            var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
            var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");return views;
        }
    }
}
