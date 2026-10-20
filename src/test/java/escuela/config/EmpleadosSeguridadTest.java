package escuela.config;

import org.springframework.context.annotation.*;
import org.springframework.mock.web.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.web.bind.annotation.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class EmpleadosSeguridadTest {
 private MockHttpSession sesion(String permiso){var s=new MockHttpSession();s.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(UsernamePasswordAuthenticationToken.authenticated("qa","",List.of(new SimpleGrantedAuthority(permiso)))));return s;}
 @Test void permisosAcademicosNoAbrenPersonalYEscrituraLaboralTienePermisoPropioYCsrf() throws Exception {
  try(var c=new AnnotationConfigWebApplicationContext()){
   c.setServletContext(new MockServletContext());c.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,Paginas.class);c.refresh();
   var mvc=MockMvcBuilders.webAppContextSetup(c).addFilters(c.getBean(FilterChainProxy.class)).build();
   for(String permiso:List.of("MAESTRO_LEER","MAESTRO_ADMINISTRAR","PORTAL_MAESTRO_ACCEDER","PORTAL_TUTOR_ACCEDER"))
    assertThat(mvc.perform(get("/admin/empleados/1").session(sesion(permiso))).andReturn().getResponse().getStatus()).isEqualTo(403);
   assertThat(mvc.perform(get("/admin/empleados/1").session(sesion("EMPLEADO_LEER"))).andReturn().getResponse().getStatus()).isEqualTo(200);
   for(String ruta:List.of("/admin/empleados/1","/admin/empleados/1/laboral","/admin/empleados/1/planteles","/admin/empleados/1/planteles/2/finalizar","/admin/empleados/1/planteles/cambio-principal"))
    assertThat(mvc.perform(post(ruta).session(sesion("EMPLEADO_LABORAL_ADMINISTRAR"))).andReturn().getResponse().getStatus()).isIn(302,403);
   var session=sesion("EMPLEADO_ADMINISTRAR");
   var token=mvc.perform(get("/acceso-qa-csrf").session(session)).andReturn().getRequest().getAttribute("_csrf");
   var csrf=(org.springframework.security.web.csrf.CsrfToken)token;
   assertThat(mvc.perform(post("/admin/empleados/1/laboral").session(session).param(csrf.getParameterName(),csrf.getToken())).andReturn().getResponse().getStatus()).isEqualTo(403);
   assertThat(mvc.perform(post("/admin/empleados/1/planteles/cambio-principal").session(session).param(csrf.getParameterName(),csrf.getToken())).andReturn().getResponse().getStatus()).isEqualTo(403);
   var laboral=sesion("EMPLEADO_LABORAL_ADMINISTRAR");
   var tokenLaboral=(org.springframework.security.web.csrf.CsrfToken)mvc.perform(get("/acceso-qa-csrf").session(laboral)).andReturn().getRequest().getAttribute("_csrf");
   assertThat(mvc.perform(post("/admin/empleados/1/planteles/cambio-principal").session(laboral).param(tokenLaboral.getParameterName(),tokenLaboral.getToken())).andReturn().getResponse().getStatus()).isEqualTo(200);
   assertThat(mvc.perform(post("/admin/empleados/1").session(session).param(csrf.getParameterName(),csrf.getToken())).andReturn().getResponse().getStatus()).isEqualTo(200);
   assertThat(mvc.perform(get("/admin/empleados/1")).andReturn().getResponse().getStatus()).isIn(302,401,403);
  }
 }
 @RestController static class Paginas {
  @GetMapping({"/admin/empleados/1","/acceso-qa-csrf"}) String get(){return "QA";}
  @PostMapping({"/admin/empleados/1","/admin/empleados/1/laboral","/admin/empleados/1/planteles","/admin/empleados/1/planteles/2/finalizar","/admin/empleados/1/planteles/cambio-principal"}) String post(){return "QA";}
 }
}
