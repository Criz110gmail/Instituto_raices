package escuela.personal;

import escuela.personal.service.*;
import escuela.personal.controller.PreparacionNominaController;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.nio.file.*;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class NominaDetalleOrganizadoTest {
 private BigDecimal n(String s){return new BigDecimal(s);}
 private String render(String estado,boolean financiero,int revision)throws Exception{
  var s=mock(PreparacionNominaService.class);var p=mock(ProgramacionNominaService.class);var e=mock(EmpleadoService.class);var a=mock(AplicacionNominaService.class);
  var permisos=new ArrayList<>(List.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR"));if(financiero)permisos.add("MOVIMIENTO_FINANCIERO_LEER");
  var au=permisos.stream().map(SimpleGrantedAuthority::new).toList();when(e.autorizarSalarios(false)).thenReturn(new UsuarioPrincipal(1L,1L,Set.of(),true,false,"qa","",au));when(e.permitido("CONDICION_SALARIAL_ADMINISTRAR")).thenReturn(true);
  var inicio=LocalDate.of(2027,1,1);var fin=LocalDate.of(2027,1,31);
  var b=new PreparacionNominaService.Borrador(1L,2L,"Nómina ficticia de capacitación",estado,2,0L,"Motivo ficticio","ENE-QA","Enero de prueba","Mensual",inicio,fin,fin,"MXN","sello",1,n("12500"));
  when(s.obtener(1L)).thenReturn(b);when(s.cobertura(1L,revision)).thenReturn(new PreparacionNominaService.Cobertura("Mensual","ENE-QA","Enero de prueba",inicio,fin,fin,"MXN"));
  when(s.totalesGuardados(1L,revision)).thenReturn(new PreparacionNominaService.Totales(1,n("12000"),n("500"),n("0"),n("0"),n("12500")));
  when(s.guardados(1L,revision,0)).thenReturn(new PageImpl<>(List.of(new PreparacionNominaService.Guardado(7L,3L,"EMP-QA","Persona Ficticia",n("12000"),n("500"),n("0"),n("0"),n("12500"),"MXN","sello",revision))));
  when(s.historial(1L,0)).thenReturn(Page.empty());when(s.revisiones(1L,0)).thenReturn(new PageImpl<>(List.of(new PreparacionNominaService.Revision(1,"24/10/2026 10:00","qa","Registro"),new PreparacionNominaService.Revision(2,"24/10/2026 11:00","qa","Revisión"))));
  if(estado.equals("APLICADA"))when(a.obtener(1L)).thenReturn(new AplicacionNominaService.Aplicacion(37L,"BANCO-QA · Cuenta ficticia","24/10/2026 10:54","qa",n("12500"),"MXN","Motivo de aplicación ficticio"));
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(new PreparacionNominaController(s,p,e,a)).setViewResolvers(views).build();var r=mvc.perform(get("/admin/preparacion-nomina/1").param("revisionConsulta",Integer.toString(revision)).param("pagina","2").param("q","capacitacion")).andReturn().getResponse();assertThat(r.getStatus()).isEqualTo(200);
  var dir=Path.of("target/qa-nomina-organizacion");Files.createDirectories(dir);Files.writeString(dir.resolve(estado.toLowerCase()+"-"+financiero+"-"+revision+".html"),r.getContentAsString());return r.getContentAsString();
 }
 @Test void aplicadaDestacaEstadoAplicacionYMovimientoExactoSinOfrecerMutaciones()throws Exception{
  var html=render("APLICADA",true,2);assertThat(html).contains("Nómina aplicada","$12,500.00 MXN","Detalle por empleado","data-tab-hash=\"#aplicacion\"","Ver egreso de esta nómina","movimientoId=37","institucionId=1","clase=NOMINA","Motivo de aplicación ficticio");
  assertThat(html).doesNotContain("preparar-actualizacion","/cancelar","Revisar y aplicar nómina","No autorizado");
  assertThat(render("APLICADA",false,2)).doesNotContain("Ver egreso de esta nómina","movimientoId=37");
 }
 @Test void historicaConservaRevisionConsultaEnExcelYPaginacionSinConfundirConEstadoActual()throws Exception{var html=render("APLICADA",true,1);assertThat(html).contains("Histórica, no es la última revisión","revisionConsulta=1","Ver revisión 1","Ver revisión 2","pagina=2");}
 @Test void borradorYCanceladoConservanConsultaYAccionesSegunEstado()throws Exception{
  assertThat(render("BORRADOR",false,2)).contains("Borrador pendiente de autorizar","Autorización y pagos por empleado","Gestión del borrador","preparar-actualizacion","Revisar cancelación","data-money-confirm=\"nomina-borrador-cancelar\"").doesNotContain("tab-nomina-aplicacion");
  assertThat(render("CANCELADO",false,2)).contains("Borrador cancelado","sólo consulta").doesNotContain("preparar-actualizacion","/cancelar","tab-nomina-gestion");
 }
}
