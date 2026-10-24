package escuela.personal;
import escuela.personal.dto.*;import escuela.admin.dto.ModuloCatalogo;
import org.junit.jupiter.api.Test;import org.springframework.stereotype.Controller;import org.springframework.ui.Model;import org.springframework.web.bind.annotation.*;import org.springframework.test.web.servlet.setup.MockMvcBuilders;import org.thymeleaf.spring6.SpringTemplateEngine;import org.thymeleaf.spring6.view.ThymeleafViewResolver;import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;import org.springframework.data.domain.Page;
import java.util.*;import static org.assertj.core.api.Assertions.*;import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
class EmpleadoRenderTest {
 @Test void exportaFixturesFicticiasParaRevisionVisual()throws Exception{var dir=java.nio.file.Path.of("target/qa-personal");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve("empleado.html"),render("/test/empleado",true,true));java.nio.file.Files.writeString(dir.resolve("empleados.html"),render("/test/empleados",true,true));}
 @Controller static class Vista {
  @GetMapping("/test/resumen-laboral") String resumen(@RequestParam(defaultValue="ACTIVO")String situacion,@RequestParam(defaultValue="false")boolean tabla,@RequestParam(defaultValue="true")boolean laboral,Model m){
   String plantilla=render(true,laboral,m);var e=(EmpleadoVista)m.getAttribute("empleado");e=e.conResumenLaboral(new ResumenLaboral(situacion,java.time.LocalDate.of(2026,10,1),situacion.equals("BAJA")?java.time.LocalDate.of(2026,10,20):null));m.addAttribute("empleado",e);
   if(tabla){plantilla=listado(m);m.addAttribute("puedeLaboral",laboral);m.addAttribute("resultado",new org.springframework.data.domain.PageImpl<>(List.of(e)));}return plantilla;
  }
  @GetMapping("/test/menu-puestos") String menuPuestos(@RequestParam(defaultValue="true")boolean laboral,Model m){var vista=listado(m);var permisos=new HashSet<>(Set.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER"));if(laboral)permisos.add("CONDICION_SALARIAL_LEER");m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(permisos)).toList());return vista;}
  @GetMapping("/test/empleados-estados") String estados(Model m){String vista=listado(m);m.addAttribute("resultado",new org.springframework.data.domain.PageImpl<>(List.of(
   new EmpleadoVista(2L,1L,"EMP-QA-ACT","Ana","Prueba",null,null,null,true,false,0L,7L,true,"DOC-QA-ACT",null),
   new EmpleadoVista(3L,1L,"EMP-QA-INACT","Ana","Inactiva",null,null,null,false,false,0L,8L,false,"DOC-QA-INACT",null))));return vista;}
  @GetMapping("/test/empleado-cambio-programado") String programado(Model m){String vista=planteles(false,m);m.addAttribute("resumenPrincipal",new escuela.personal.service.EmpleadoService.ResumenPrincipal(java.time.LocalDate.of(2026,10,20),"Plantel Centro de Prueba","Plantel Norte",java.time.LocalDate.of(2026,10,21)));m.addAttribute("proximas",new org.springframework.data.domain.PageImpl<>(List.of(new escuela.personal.service.EmpleadoService.Adscripcion(34L,0L,"Plantel Norte","21/10/2026","Sin fin",true))));return vista;}
  @GetMapping("/test/empleado-principal-sin-cambio") String sinCambio(Model m){String vista=planteles(false,m);m.addAttribute("resumenPrincipal",new escuela.personal.service.EmpleadoService.ResumenPrincipal(java.time.LocalDate.of(2026,10,20),"Plantel Centro de Prueba",null,null));return vista;}
  @GetMapping("/test/empleado-planteles") String planteles(@RequestParam(defaultValue="false")boolean lectura,Model m){String vista=render(true,true,m);m.addAttribute("actuales",new org.springframework.data.domain.PageImpl<>(List.of(
    new escuela.personal.service.EmpleadoService.Adscripcion(31L,2L,"Plantel Centro de Prueba", "01/10/2026","Sin fin",true),
    new escuela.personal.service.EmpleadoService.Adscripcion(32L,3L,"Plantel adicional con nombre largo para comprobar la distribución móvil", "01/10/2026","Sin fin",false))));
    m.addAttribute("anteriores",new org.springframework.data.domain.PageImpl<>(List.of(new escuela.personal.service.EmpleadoService.Adscripcion(33L,4L,"Plantel anterior", "01/09/2026","30/09/2026",false))));m.addAttribute("puedeEditarLaboral",!lectura);return vista;}
  @GetMapping("/test/empleado-sin-perfil") String sinPerfil(Model m){String vista=render(true,true,m);m.addAttribute("empleado",new EmpleadoVista(2L,1L,"EMP-TEST","Ana","Prueba",null,"ana@example.test",null,true,false,1L,null,false,null,null));return vista;}
  @GetMapping("/test/empleado") String render(@RequestParam(defaultValue="true")boolean edicion,@RequestParam(defaultValue="true")boolean laboral,Model m){var e=new EmpleadoVista(2L,1L,"EMP-TEST","Ana","Prueba",null,"ana@example.test",null,true,false,1L,7L,true,"DOC-TEST","docente.qa");m.addAttribute("edicion",edicion);m.addAttribute("id",edicion?2L:null);if(edicion)m.addAttribute("empleado",e);m.addAttribute("form",e.formulario());m.addAttribute("instituciones",List.of());m.addAttribute("puedeAdministrar",true);m.addAttribute("puedeLaboral",laboral);m.addAttribute("puedeEditarLaboral",laboral);m.addAttribute("puedeDocente",true);if(laboral){var f=new LaboralForm();f.setPuesto("PRIVADO-TEST");m.addAttribute("laboralForm",f);m.addAttribute("actuales",Page.empty());m.addAttribute("proximas",Page.empty());m.addAttribute("anteriores",Page.empty());m.addAttribute("hoyLaboral",java.time.LocalDate.of(2026,10,20));m.addAttribute("fechaCambioSugerida",java.time.LocalDate.of(2026,10,21));m.addAttribute("historial",Page.empty());m.addAttribute("plantelForm",new PlantelEmpleadoForm());}return "admin/empleado-form";}
  @GetMapping("/test/empleados") String listado(Model m){m.addAttribute("modulos",List.of(ModuloCatalogo.EMPLEADOS));m.addAttribute("moduloActual",ModuloCatalogo.EMPLEADOS);m.addAttribute("resultado",Page.empty());m.addAttribute("filtro",new FiltroEmpleado(1L,"Ana","DOCENTE",0,25));m.addAttribute("puedeAdministrar",true);return "admin/empleados";}
 }
 String render(String ruta,boolean edicion,boolean laboral)throws Exception{var r=new ClassLoaderTemplateResolver();r.setPrefix("templates/");r.setSuffix(".html");r.setCharacterEncoding("UTF-8");var e=new SpringTemplateEngine();e.setTemplateResolver(r);var v=new ThymeleafViewResolver();v.setTemplateEngine(e);v.setCharacterEncoding("UTF-8");var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(v).build();var response=mvc.perform(get(ruta).param("edicion",String.valueOf(edicion)).param("laboral",String.valueOf(laboral))).andReturn().getResponse();assertThat(response.getStatus()).isEqualTo(200);return response.getContentAsString();}
 @Test void resumenGuardadoVisibleYPrivadoEnExpedienteYTabla()throws Exception{
  var r=new ClassLoaderTemplateResolver();r.setPrefix("templates/");r.setSuffix(".html");r.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(r);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(views).build();
  for(String situacion:List.of("ACTIVO","BAJA","POR_CONFIRMAR"))for(boolean tabla:List.of(true,false))for(boolean permiso:List.of(true,false)){
   var response=mvc.perform(get("/test/resumen-laboral").param("situacion",situacion).param("tabla",String.valueOf(tabla)).param("laboral",String.valueOf(permiso))).andReturn().getResponse();assertThat(response.getStatus()).isEqualTo(200);var html=response.getContentAsString();
   if(permiso){assertThat(html).contains("Situación laboral","01/10/2026",new ResumenLaboral(situacion,null,null).etiqueta());if(situacion.equals("BAJA"))assertThat(html).contains("20/10/2026");if(situacion.equals("ACTIVO"))assertThat(html).contains("class=\"status positivo\">Activo</span>");}
   else assertThat(html).doesNotContain("Situación laboral","01/10/2026","20/10/2026","employee-labor-summary");
   if(permiso&&situacion.equals("ACTIVO")){var dir=java.nio.file.Path.of("target/qa-personal");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve(tabla?"resumen-tabla.html":"resumen-ficha.html"),html);}
  }
 }
 @Test void altaYEdicionRenderizanConPestanasModalesYFormulariosPost()throws Exception{for(boolean edicion:new boolean[]{true,false}){String html=render("/test/empleado",edicion,true);assertThat(html).contains("data-student-tabs","data-money-confirm=\"empleado-registro\"","method=\"post\"","Cancelar","/css/forms.css");if(edicion)assertThat(html).contains("data-money-confirm=\"empleado-laboral\"","data-money-confirm=\"empleado-perfil\"","data-money-confirm=\"empleado-plantel\"","PRIVADO-TEST","/planteles/buscar");}}
 @Test void puestoLaboralUsaAyudaCompartidaYConservaTextoAnteriorSinCrearOtraFuenteSalarial()throws Exception{
  assertThat(render("/test/empleado",true,true)).contains("/admin/empleados/2/puestos/buscar","id=\"laboral-puesto\"","name=\"puestoId\"","autocomplete-input","data-no-scope","data-refresh-on-focus=\"true\"","value=\"PRIVADO-TEST\"","no asigna sueldo ni cambia nóminas");
 }
 @Test void sinPermisoLaboralNoSeRenderizaInformacionNiCamposPrivados()throws Exception{assertThat(render("/test/empleado",true,false)).contains("Datos personales","Cuenta del portal docente").doesNotContain("PRIVADO-TEST","name=\"puesto\"","name=\"puestoId\"","/puestos/buscar","name=\"situacion\"","name=\"motivoBaja\"","Historial laboral");}
 @Test void filtrosYExcelConservanMismosParametros()throws Exception{assertThat(render("/test/empleados",true,true)).contains("/admin/empleados/excel?institucionId=1&amp;q=Ana&amp;estado=DOCENTE","Personal y nómina","name=\"q\"","name=\"estado\"","name=\"tamanio\"");}
 @Test void menuMuestraPuestosJuntoAEmpleadosSoloConPermisoSalarial()throws Exception{
  var html=render("/test/menu-puestos",true,true);assertThat(html).contains("href=\"/admin/puestos\"","Puestos laborales");assertThat(html.indexOf("href=\"/admin/empleados\"")).isLessThan(html.indexOf("href=\"/admin/puestos\""));
  assertThat(html.split("Administración · Personal y nómina",-1)).hasSize(3);
  assertThat(render("/test/menu-puestos",true,false)).doesNotContain("href=\"/admin/puestos\"","Puestos laborales");
 }
 @Test void listadoUsaElMismoEstadoVerdeCompartidoYNoColoreaInactivos()throws Exception{
  var html=render("/test/empleados-estados",true,true);
  assertThat(html).contains("class=\"status positivo\">Activo</span>","class=\"status neutro\">Inactivo</span>","name=\"estado\"","/admin/empleados/excel?institucionId=1&amp;q=Ana&amp;estado=DOCENTE");
  assertThat(html.split("class=\"status positivo\"",-1)).hasSize(3);assertThat(html.split("class=\"status neutro\"",-1)).hasSize(3);
  var dir=java.nio.file.Path.of("target/qa-personal");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve("empleados-estados.html"),html);
 }
 @Test void fotografiaYCodigoDocenteUsanControlesCompartidosSinOcultarNombreDePestana()throws Exception{
  String html=render("/test/empleado-sin-perfil",true,true);
  assertThat(html).contains("Información docente","employee-photo-form","employee-photo-input","id=\"empleado-fotografia\"","aria-describedby=\"empleado-fotografia-ayuda\"","<div class=\"form-grid\"><label class=\"wide\" for=\"empleado-codigo-docente\"","Habilitar perfil docente");
  var css=java.nio.file.Files.readString(java.nio.file.Path.of("src/main/resources/static/css/forms.css"));assertThat(css).contains(".employee-photo-input::file-selector-button",".employee-card .student-tablist.payment-record-tabs","white-space:normal; text-overflow:clip; overflow:visible");
  var directorio=java.nio.file.Path.of("target/qa-personal");java.nio.file.Files.createDirectories(directorio);java.nio.file.Files.writeString(directorio.resolve("empleado-sin-perfil.html"),html);
 }
 @Test void tarjetasPlantelConFechasSeparadasYAccionSóloParaVigenciasAbiertas()throws Exception{
  String html=render("/test/empleado-planteles",true,true);
  assertThat(html).contains("employee-campus-card","Plantel principal","Plantel adicional","Inicio de la asignación","Fin de la asignación","Sin fecha de fin","30/09/2026","data-money-confirm=\"empleado-vigencia\"","/planteles/31/finalizar","/planteles/32/finalizar").doesNotContain("/planteles/33/finalizar");
  assertThat(html.split("class=\"employee-campus-card",-1)).hasSize(4);
  assertThat(html).contains("Asignaciones actuales","Ver asignaciones anteriores","Finalizada","data-campus-change","data-campus-dialog","data-campus-cancel","data-campus-confirm");
  var r=new ClassLoaderTemplateResolver();r.setPrefix("templates/");r.setSuffix(".html");r.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(r);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new Vista()).setViewResolvers(views).build();
  String lectura=mvc.perform(get("/test/empleado-planteles").param("lectura","true")).andReturn().getResponse().getContentAsString();
  assertThat(lectura).contains("Plantel Centro de Prueba","employee-campus-card").doesNotContain("/planteles/31/finalizar","employee-campus-close-controls","Agregar asignación laboral");
  var dir=java.nio.file.Path.of("target/qa-personal");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve("empleado-planteles.html"),html);
 }
 @Test void avisoAclaraPrincipalActualYFechaFuturaSinPedirOtraConfirmacion()throws Exception{
  var html=render("/test/empleado-cambio-programado",true,true);
  assertThat(html).contains("Cambio de principal programado","Hoy · 20/10/2026","Desde el 21/10/2026","Plantel Centro de Prueba","Plantel Norte","Principal actual","Próximo plantel principal","No necesitas confirmar el cambio otra vez","Próximas asignaciones");
  assertThat(html.indexOf("employee-campus-schedule")).isLessThan(html.indexOf("Asignaciones actuales"));
  var sinCambio=render("/test/empleado-principal-sin-cambio",true,true);assertThat(sinCambio).contains("Principal vigente hoy","No hay otro plantel principal programado").doesNotContain("Cambio de principal programado","Desde el null");
  var dir=java.nio.file.Path.of("target/qa-personal");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve("empleado-programado.html"),html);
 }
}
