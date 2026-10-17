package escuela.admin.controller;

import java.nio.file.*;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class PerfilSesionRenderTest {
 @Test void fragmentoCompartidoRenderizaPerfilUnicoSoloLecturaYNombreEscapado() throws Exception {
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);
  var vistas=new ThymeleafViewResolver();vistas.setTemplateEngine(engine);vistas.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(vistas).build();
  var response=mvc.perform(get("/qa/perfil")).andReturn().getResponse();
  assertThat(response.getStatus()).isEqualTo(200);var html=response.getContentAsString();
  assertThat(html).contains("data-session-profile-open","aria-haspopup=\"dialog\"","id=\"session-profile\"",
   "data-session-profile-close","/css/session-profile.css?v=20261016-foto","/js/session-profile.js?v=20261016-foto",
   "data-session-photo-open","id=\"session-photo-preview\"","data-session-photo-close","data-session-photo-image",
   "&lt;usuario.prueba&gt;","/admin/perfil/fotografia","method=\"post\"");
  assertThat(html).doesNotContain("<usuario.prueba>","password","data-money-confirm");
  assertThat(html.split("id=\"session-profile\"",-1)).hasSize(2);
  Path dir=Path.of("target/qa-perfil");Files.createDirectories(dir);
  Files.writeString(dir.resolve("perfil.html"),"<!doctype html><html lang=\"es\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>Perfil · QA ficticio</title><link rel=\"stylesheet\" href=\"/css/admin.css\"><link rel=\"stylesheet\" href=\"/css/forms.css\"></head><body><main style=\"padding:24px\"><header class=\"topbar\"><h1>Mi espacio</h1>"+html+"</header><p>Prueba aislada con información ficticia. No modifica usuarios ni finanzas.</p></main></body></html>");
 }
 @Controller static class Pantalla {
  @GetMapping("/qa/perfil") String vista(Model m){m.addAttribute("usuarioSesion","<usuario.prueba>");return "admin/fragments/session-actions :: controls";}
 }
}
