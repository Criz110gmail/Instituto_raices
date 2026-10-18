package escuela.pendiente.controller;

import escuela.pendiente.dto.*;
import escuela.pendiente.entity.EstadoPendiente;
import escuela.pendiente.service.PendienteService;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.springframework.data.domain.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

class PendienteRenderTest {
 @Test void tresVistasRealesEnAmbosPortalesConFechasEscapadoYPaginacion()throws Exception{
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");var mvc=MockMvcBuilders.standaloneSetup(new Pantalla()).setViewResolvers(views).build();
  var dir=Path.of("target/qa-pendientes");Files.createDirectories(dir);
  for(String portal:List.of("admin","maestros"))for(String vista:List.of("lista","formulario","detalle")){
   var r=mvc.perform(get("/qa/"+portal+"/"+vista)).andReturn().getResponse();assertThat(r.getStatus()).isEqualTo(200);String h=r.getContentAsString();
   assertThat(h).contains("/"+portal+"/pendientes","Preparar material &lt;privado&gt;").doesNotContain("<script>alert");
   if(!vista.equals("lista"))assertThat(h).contains("data-money-confirm=\"pendiente-", "name=\"version\"","/js/money-input.js?v=20261017-pendientes");
   if(vista.equals("formulario"))assertThat(h).contains("name=\"fechaCompromiso\"","2026-10-20","maxlength=\"180\"");
   if(vista.equals("lista")){assertThat(h).contains("20/10/2026","Exportar Excel","Siguiente");if(portal.equals("maestros"))assertThat(h).doesNotContain("/admin/perfil/fotografia");}
   Files.writeString(dir.resolve(portal+"-"+vista+".html"),h);
  }
 }
 @Controller static class Pantalla {
  @GetMapping("/qa/{portal}/{vista}") String vista(@org.springframework.web.bind.annotation.PathVariable String portal,@org.springframework.web.bind.annotation.PathVariable String vista,Model m){
   var p=new PendienteVista(3L,"Preparar material <privado>","Descripción <script>alert(1)</script>",LocalDate.of(2026,10,20),EstadoPendiente.PENDIENTE,2L,null,false);
   m.addAttribute("base","/"+portal+"/pendientes");m.addAttribute("docente",portal.equals("maestros"));m.addAttribute("usuarioSesion","usuario.qa");m.addAttribute("estados",EstadoPendiente.values());m.addAttribute("form",p.formulario());m.addAttribute("id",3L);m.addAttribute("pendiente",p);
   m.addAttribute("filtro",new PendienteFiltro("","ABIERTOS",null,null,0,25));m.addAttribute("resultado",new PageImpl<>(List.of(p),PageRequest.of(0,25),30));m.addAttribute("avisos",new PendienteService.Avisos(1,2,3,6,List.of(p)));m.addAttribute("historial",new PageImpl<>(List.of(new PendienteService.Cambio("CREADO","Pendiente","17/10/2026 18:30",2L))));return "pendientes/"+vista;
  }
 }
}
