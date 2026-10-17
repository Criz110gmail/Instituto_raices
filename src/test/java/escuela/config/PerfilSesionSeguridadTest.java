package escuela.config;

import escuela.admin.controller.PerfilSesionController;
import escuela.admin.service.PerfilSesionService;
import escuela.seguridad.service.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class PerfilSesionSeguridadTest {
 @Test void soloSesionPropiaSinPermisoDeUsuariosNiIdentificadorSolicitable() throws Exception {
  try(var context=new AnnotationConfigWebApplicationContext()) {
   context.setServletContext(new MockServletContext());
   context.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,
     PerfilSesionController.class,Config.class);context.refresh();
   var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(context.getBean(FilterChainProxy.class)).build();
   var service=context.getBean(PerfilSesionService.class);
   var principal=new UsuarioPrincipal(7L,1L,Set.of(2L),false,false,"qa","hash-no-publicar",
     List.of(new SimpleGrantedAuthority("GUIA_PROCESOS_CONSULTAR")));
   when(service.actual(principal)).thenReturn(new PerfilSesionService.Resumen("qa","qa@example.com","Institución QA","Administración","1 plantel autorizado"));
   var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(
     UsernamePasswordAuthenticationToken.authenticated(principal,"",principal.authorities())));
   var r=mvc.perform(get("/admin/perfil/resumen").param("usuarioId","999").session(session)).andReturn().getResponse();
   assertThat(r.getStatus()).isEqualTo(200);
   assertThat(r.getHeader("Cache-Control")).contains("no-store");
   assertThat(r.getContentAsString()).contains("qa@example.com","1 plantel autorizado").doesNotContain("hash-no-publicar","password","999","authorities");
   verify(service).actual(principal);
  }
 }
 @Test void anonimoNoPuedeConsultarDatos() throws Exception {
  try(var context=new AnnotationConfigWebApplicationContext()) {
   context.setServletContext(new MockServletContext());
   context.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,
     PerfilSesionController.class,Config.class);context.refresh();
   var mvc=MockMvcBuilders.webAppContextSetup(context).addFilters(context.getBean(FilterChainProxy.class)).build();
   var r=mvc.perform(get("/admin/perfil/resumen").accept(org.springframework.http.MediaType.APPLICATION_JSON)).andReturn().getResponse();
   // La negociación de contenido puede rechazar JSON directamente o dirigir al acceso.
   assertThat(r.getStatus()).isIn(302,401,403);
   if(r.getStatus()==302)assertThat(r.getRedirectedUrl()).contains("/login");
   assertThat(r.getContentAsString()).doesNotContain("qa@example.com","Institución QA");
   verifyNoInteractions(context.getBean(PerfilSesionService.class));
  }
 }
 @Configuration static class Config {
  @Bean PerfilSesionService perfil(){return mock(PerfilSesionService.class);}
  @Bean FotografiaUsuarioService fotografia(){return mock(FotografiaUsuarioService.class);}
 }
}
