package escuela.config;

import escuela.inicio.controller.InicioController;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class SitioPublicoSeguridadTest {
    @Test void inicioYRecursosSonPublicosSinAbrirExpedientesNiCrearSesion() throws Exception {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.register(LoginReinicioSeguridadTest.Config.class, SeguridadConfig.class,
                    SesionExpiradaAccessDeniedHandler.class, InicioController.class,
                    LoginRenderSinSesionTest.Vistas.class, Recursos.class);
            context.refresh();
            var mvc = MockMvcBuilders.webAppContextSetup(context)
                    .addFilters(context.getBean(FilterChainProxy.class)).build();
            var inicio = mvc.perform(get("/")).andReturn();
            assertThat(inicio.getResponse().getStatus()).isEqualTo(200);
            assertThat(inicio.getRequest().getSession(false)).isNull();
            assertThat(inicio.getResponse().getContentAsString()).contains("TRANSFORMANDO CULTURA.",
                    "href=\"/personal/acceso\"", "href=\"/login\"", "/images/logo.png", "/images/imagen.png");
            for (var ruta : new String[]{"/images/logo.png", "/images/imagen.png", "/css/sitio-escolar.css", "/js/sitio-escolar.js"}) {
                var recurso = mvc.perform(get(ruta)).andReturn();
                assertThat(recurso.getResponse().getStatus()).as(ruta).isEqualTo(200);
                assertThat(recurso.getResponse().getContentAsByteArray()).isNotEmpty();
            }
            for (var ruta : new String[]{"/images/privado.png", "/admin/alumnos/1/documentos/1", "/personal/recibos/PAGO/1/pdf"}) {
                assertThat(mvc.perform(get(ruta)).andReturn().getResponse().getStatus()).as(ruta).isIn(302, 401, 403);
            }
        }
    }
    @Configuration static class Recursos implements WebMvcConfigurer {
        @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/images/**").addResourceLocations("classpath:/static/images/");
            registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
            registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
        }
    }
}
