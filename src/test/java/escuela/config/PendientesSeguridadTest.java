package escuela.config;

import escuela.pendiente.controller.PendienteController;
import escuela.pendiente.dto.*;
import escuela.pendiente.entity.EstadoPendiente;
import escuela.pendiente.service.PendienteService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.security.access.AccessDeniedException;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class PendientesSeguridadTest {
 private AnnotationConfigWebApplicationContext contexto(){var c=new AnnotationConfigWebApplicationContext();c.setServletContext(new MockServletContext());c.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,PendienteController.class,Config.class);c.refresh();return c;}
 private UsuarioPrincipal p(String derecho){return new UsuarioPrincipal(7L,1L,Set.of(),true,false,"qa","NO-EXPONER",List.of(new SimpleGrantedAuthority(derecho)));}
 private MockHttpSession sesion(UsuarioPrincipal p){var s=new MockHttpSession();s.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(UsernamePasswordAuthenticationToken.authenticated(p,"",p.authorities())));return s;}
 @Test void avisosDeAmbosPortalesIgnoranIdDeUsuarioSolicitadoYNoExponenPrincipal()throws Exception{
  try(var c=contexto()){var mvc=MockMvcBuilders.webAppContextSetup(c).addFilters(c.getBean(FilterChainProxy.class)).build();var s=c.getBean(PendienteService.class);
   for(boolean docente:List.of(false,true)){var p=p(docente?"PORTAL_MAESTRO_ACCEDER":"GUIA_PROCESOS_CONSULTAR");when(s.avisos(p,docente)).thenReturn(new PendienteService.Avisos(0,1,0,1,List.of(new PendienteVista(3L,"Nota propia","",LocalDate.of(2026,10,20),EstadoPendiente.PENDIENTE,0L,null,false))));
    var r=mvc.perform(get((docente?"/maestros":"/admin")+"/pendientes/avisos").param("usuarioId","999").param("maestroId","888").session(sesion(p))).andReturn().getResponse();
    assertThat(r.getStatus()).isEqualTo(200);assertThat(r.getHeader("Cache-Control")).contains("no-store");assertThat(r.getContentAsString()).contains("Nota propia").doesNotContain("NO-EXPONER","password","usuarioId","authorities","999");verify(s).avisos(p,docente);
   }
  }
 }
 @Test void anonimosYFamiliaNoAccedenYSoporteNoSuplantaMaestro()throws Exception{
  try(var c=contexto()){var mvc=MockMvcBuilders.webAppContextSetup(c).addFilters(c.getBean(FilterChainProxy.class)).build();var s=c.getBean(PendienteService.class);
   assertThat(mvc.perform(get("/admin/pendientes/avisos")).andReturn().getResponse().getStatus()).isIn(302,401,403);
   var familia=p("PORTAL_TUTOR_ACCEDER");when(s.avisos(familia,false)).thenThrow(new AccessDeniedException("Sin acceso"));
   assertThat(mvc.perform(get("/admin/pendientes/avisos").session(sesion(familia))).andReturn().getResponse().getStatus()).isEqualTo(403);
   var soporte=p("PORTAL_MAESTRO_SOPORTE");assertThat(mvc.perform(get("/maestros/pendientes/avisos").session(sesion(soporte))).andReturn().getResponse().getStatus()).isEqualTo(403);verify(s,never()).avisos(soporte,true);
  }
 }
 @Test void postSinCsrfNoPuedeGuardarNiCambiarEstado()throws Exception{
  try(var c=contexto()){var mvc=MockMvcBuilders.webAppContextSetup(c).addFilters(c.getBean(FilterChainProxy.class)).build();var s=c.getBean(PendienteService.class);
   for(String ruta:List.of("/admin/pendientes","/admin/pendientes/3","/admin/pendientes/3/estado"))assertThat(mvc.perform(post(ruta).session(sesion(p("GUIA_PROCESOS_CONSULTAR"))).param("titulo","Nota").param("estado","REALIZADO")).andReturn().getResponse().getStatus()).isIn(302,403);
   verifyNoInteractions(s);
  }
 }
 @Configuration static class Config {@Bean PendienteService pendientes(){return mock(PendienteService.class);}}
}
