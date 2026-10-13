package escuela.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class LoginPestanasCsrfTest {
 @Test void tokenDePestanaViejaSeRecuperaYTokenActualPermiteSoloElEnvioProtegido()throws Exception {
  var filter=new CsrfFilter(new HttpSessionCsrfTokenRepository());
  filter.setAccessDeniedHandler(new SesionExpiradaAccessDeniedHandler());
  var endpoint=new EnvioPrueba();
  var mvc=MockMvcBuilders.standaloneSetup(new SesionController(),endpoint).addFilters(filter).build();
  for(String origen:new String[]{"familias","admin","maestros"}) {
   var antiguo=mvc.perform(get("/acceso/csrf")).andReturn();
   var sesionAntigua=(MockHttpSession)antiguo.getRequest().getSession(false);
   var viejo=token(antiguo.getResponse().getContentAsString());sesionAntigua.invalidate();
   var actual=mvc.perform(get("/acceso/csrf")).andReturn();
   var sesionActual=(MockHttpSession)actual.getRequest().getSession(false);
   var nuevo=token(actual.getResponse().getContentAsString());
   int previos=endpoint.envios;
   var rechazado=mvc.perform(post("/login").session(sesionActual).param("origen",origen).param("_csrf",viejo)).andReturn().getResponse();
   String acceso=origen.equals("familias")?"/familias":origen.equals("maestros")?"/maestros/acceso":"/login";
   assertThat(rechazado.getRedirectedUrl()).isEqualTo(acceso+"?accesoActualizado");
   assertThat(endpoint.envios).isEqualTo(previos);
   var aceptado=mvc.perform(post("/login").session(sesionActual).param("origen",origen).param("_csrf",nuevo)).andReturn().getResponse();
   assertThat(aceptado.getStatus()).isEqualTo(200);assertThat(endpoint.envios).isEqualTo(previos+1);
  }
 }
 private String token(String json){var matcher=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(json);assertThat(matcher.find()).isTrue();return matcher.group(1);}
 @RestController static class EnvioPrueba {
  int envios;
  @PostMapping("/login")String enviar(){envios++;return "prueba de token, sin autenticar usuarios";}
 }
}
