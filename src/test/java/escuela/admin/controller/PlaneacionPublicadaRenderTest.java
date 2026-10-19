package escuela.admin.controller;

import escuela.docente.dto.*;
import escuela.docente.entity.*;
import escuela.docente.service.*;
import java.time.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class PlaneacionPublicadaRenderTest {
 @Test void publicarRedirigeYDetalleMuestraVersionHistoricaYReaperturaSin500()throws Exception{
  var service=mock(PlaneacionService.class);var doc=mock(PlaneacionDocumento.class);
  when(doc.planeacionId()).thenReturn(22L);when(doc.estado()).thenReturn("Publicada");when(doc.maestro()).thenReturn("Docente Ficticio");when(doc.grado()).thenReturn("Primero");when(doc.grupo()).thenReturn("A");when(doc.plantel()).thenReturn("Plantel QA");when(doc.ciclo()).thenReturn("2026-2027");when(doc.fechaInicio()).thenReturn(LocalDate.of(2026,10,19));when(doc.fechaFin()).thenReturn(LocalDate.of(2026,10,23));when(doc.proposito()).thenReturn("Resolver sumas.");
  when(doc.materias()).thenReturn(List.of(new PlaneacionDocumento.Materia(3L,"MAT","Matemáticas")));
  when(service.detalleAdmin(22L)).thenReturn(doc);when(service.versionEntidad(22L)).thenReturn(3L);
  Instant publicada=Instant.parse("2026-10-19T22:00:00Z");
  when(service.versiones(22L)).thenReturn(List.of(new VersionPlaneacionFila(2,LocalDateTime.of(2026,10,19,17,30)),new VersionPlaneacionFila(1,LocalDateTime.of(2026,10,19,16,0))));when(service.historial(22L)).thenReturn(List.of(new HistorialPlaneacionFila(EstadoPlaneacion.EN_REVISION,EstadoPlaneacion.PUBLICADA,null,"admin.qa",publicada)));
  var controller=new PlaneacionAdminController(service,mock(ExcelPlaneacionService.class),mock(PdfPlaneacionService.class));
  var templates=new ClassLoaderTemplateResolver();templates.setPrefix("templates/");templates.setSuffix(".html");templates.setCharacterEncoding("UTF-8");var engine=new SpringTemplateEngine();engine.setTemplateResolver(templates);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  var mvc=MockMvcBuilders.standaloneSetup(controller).setViewResolvers(views).build();
  var auth=UsernamePasswordAuthenticationToken.authenticated("admin.qa","",List.of(new SimpleGrantedAuthority("PLANEACION_ADMINISTRAR")));
  var post=mvc.perform(post("/admin/planeaciones/22/publicar").param("version","2").principal(auth)).andReturn();assertThat(post.getResponse().getRedirectedUrl()).isEqualTo("/admin/planeaciones/22");assertThat(post.getFlashMap().get("pestanaPlaneacion")).isEqualTo("versiones");verify(service).publicar(22L,2L);
  var get=mvc.perform(get("/admin/planeaciones/22").principal(auth)).andReturn().getResponse();assertThat(get.getStatus()).isEqualTo(200);
  assertThat(get.getContentAsString()).contains("Publicada","Historial de versiones publicadas","/admin/planeaciones/22/versiones/1/pdf","/admin/planeaciones/22/versiones/2/pdf","Reabrir para edición",
   "Ver PDF · Versión 1 — Publicación inicial","Ver PDF · Versión 2 — Nueva publicación","Publicado el 19/10/2026 · 16:00","Publicado el 19/10/2026 · 17:30","<time", "data-student-tab=\"versiones\"", "data-student-tab=\"historial\"", "data-student-tab=\"documento\"", "data-student-tab=\"revision\"", "/js/student-tabs.js");
  var after=mvc.perform(get("/admin/planeaciones/22").principal(auth).flashAttr("pestanaPlaneacion","versiones")).andReturn();assertThat(after.getResponse().getContentAsString()).contains("data-force-active-tab=\"versiones\"");
  var dir=java.nio.file.Path.of("target/qa-planeacion");java.nio.file.Files.createDirectories(dir);
  for(var entry:java.util.Map.of("Publicada","reabrir","En revisión","publicar","Enviada","revision").entrySet()){
   when(doc.estado()).thenReturn(entry.getKey());var response=mvc.perform(get("/admin/planeaciones/22").principal(auth)).andReturn().getResponse();
   assertThat(response.getStatus()).isEqualTo(200);assertThat(response.getContentAsString()).contains("data-planeacion-confirm=\""+entry.getValue()+"\"","data-planeacion-confirm-dialog","/js/planeacion-confirmacion.js","/css/planeacion-confirmacion.css");
   if(entry.getValue().equals("publicar"))assertThat(response.getContentAsString()).contains("data-planeacion-confirm=\"ajustes\"");
   java.nio.file.Files.writeString(dir.resolve("admin-"+entry.getValue()+".html"),response.getContentAsString());
  }
  when(service.versiones(22L)).thenReturn(List.of());String empty=mvc.perform(get("/admin/planeaciones/22").principal(auth)).andReturn().getResponse().getContentAsString();assertThat(empty).contains("Todavía no hay versiones publicadas");
  doThrow(new escuela.common.exception.ReglaNegocioException("La planeación debe estar en revisión")).when(service).publicar(22L,1L);
  var rejected=mvc.perform(post("/admin/planeaciones/22/publicar").param("version","1").principal(auth)).andReturn();assertThat(rejected.getFlashMap().get("pestanaPlaneacion")).isEqualTo("revision");
 }
}
