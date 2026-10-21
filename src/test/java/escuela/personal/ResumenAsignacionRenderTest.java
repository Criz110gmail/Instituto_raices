package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.OrganizacionLaboralService.Asignacion;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class ResumenAsignacionRenderTest {
 @Controller static class Vista {
  @GetMapping("/test/asignacion-resumen") String resumen(@RequestParam String estado,@RequestParam(defaultValue="false")boolean previa,Model m){
   m.addAttribute("asignacion",new Asignacion(3L,3L,"Persona ficticia de capacitación","EMP-QA","DOC-QA · Docente de prueba",previa?"Condición individual previa":"ESQ-QA · Configuración de prueba",previa?null:"JOR-QA · Jornada de prueba",previa?null:new BigDecimal("30.00"),new BigDecimal("12000.00"),"MXN","MENSUAL","MENSUAL",LocalDate.of(2027,1,1),null,estado,"Motivo original de prueba",1L,false));
   m.addAttribute("filtro",new FiltroOrganizacionLaboral("","TODAS",0,25,3L,null,null));
   m.addAttribute("selecciones",new FiltrosCatalogosPersonal());m.addAttribute("historial",Page.empty());m.addAttribute("puedeEditar",false);m.addAttribute("volverUrl","/admin/asignaciones?empleadoId=3");
   return "admin/laboral-asignacion-detalle";
  }
 }
 @Test void resumenAgrupaDatosYExplicaCadaEstadoSinAgregarOperaciones()throws Exception{
  var r=new ClassLoaderTemplateResolver();r.setPrefix("templates/");r.setSuffix(".html");r.setCharacterEncoding("UTF-8");
  var e=new SpringTemplateEngine();e.setTemplateResolver(r);var v=new ThymeleafViewResolver();v.setTemplateEngine(e);v.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(v).build();
  var dir=Path.of("target/qa-resumen-asignacion");Files.createDirectories(dir);
  for(String estado:List.of("Próxima","Vigente","Anterior","Cancelada")){
   var response=mvc.perform(get("/test/asignacion-resumen").param("estado",estado)).andReturn().getResponse();
   assertThat(response.getStatus()).isEqualTo(200);var html=response.getContentAsString();
   assertThat(html).contains("Resumen de la asignación","Sueldo bruto base","$12,000.00","30.00","Jornada autorizada","Puesto y configuración","Vigencia registrada","01/01/2027","Sin fecha de fin","Motivo original de prueba","/css/salarial.css","#historial-asignacion").doesNotContain("Programar cambio →");
   if(estado.equals("Cancelada"))assertThat(html).contains("no aplica, aunque conserve sus fechas","El estado Cancelada impide");
   if(estado.equals("Próxima"))assertThat(html).contains("Asignación programada");
   Files.writeString(dir.resolve(estado.equals("Cancelada")?"cancelada.html":"resumen.html"),html);
  }
  var previa=mvc.perform(get("/test/asignacion-resumen").param("estado","Próxima").param("previa","true")).andReturn().getResponse().getContentAsString();
  assertThat(previa).contains("Sin registrar","Sin jornada registrada","Condición individual previa").doesNotContain(">null<");Files.writeString(dir.resolve("previa.html"),previa);
 }
}
