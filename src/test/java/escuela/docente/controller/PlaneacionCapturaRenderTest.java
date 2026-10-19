package escuela.docente.controller;

import escuela.docente.dto.*;
import escuela.docente.service.*;
import escuela.horario.service.HorarioClaseService;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class PlaneacionCapturaRenderTest {
 private final PlaneacionService servicio=mock(PlaneacionService.class);
 private MockMvc mvc(){
  var principal=new UsuarioPrincipal(9L,1L,Set.of(),false,false,"docente.qa","",List.of(new SimpleGrantedAuthority("PORTAL_MAESTRO_ACCEDER")));
  SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(principal,"",principal.authorities()));
  when(servicio.perfil(any())).thenReturn(new MaestroResponse(7L,1L,"Escuela ficticia","DOC-QA","Docente","Ficticio",null,"Docente Ficticio","docente@example.invalid",null,true,9L,"docente.qa",EstadoUsuario.ACTIVO,0L));
  when(servicio.gruposDisponibles(any(),any(),any())).thenReturn(List.of(new AsignacionMaestroResponse(1L,2L,"1 A","Plantel ficticio","2026-2027",3L,"Matemáticas",LocalDate.of(2026,10,1),null,true,0L)));
  when(servicio.materiasDisponibles(any(),any(),any(),any())).thenReturn(List.of(new PlaneacionDocumento.Materia(3L,"MAT","Matemáticas")));
  var fotos=mock(FotografiaMaestroService.class);when(fotos.actual(7L)).thenReturn(new FotografiaMaestroResponse(1L,"foto-qa.jpg","image/jpeg",123));
  var controller=new PortalMaestroController(servicio,mock(PdfPlaneacionService.class),mock(PortalAlumnoMaestroService.class),fotos,mock(HorarioClaseService.class));
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  return MockMvcBuilders.standaloneSetup(controller).setViewResolvers(views).setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver()).build();
 }
 private void fixture(String nombre,String html)throws Exception{var dir=java.nio.file.Path.of("target/qa-planeacion");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve(nombre),html);}
 private PlaneacionDocumento documento(String estado){
  var d=mock(PlaneacionDocumento.class);when(d.planeacionId()).thenReturn(22L);when(d.grupoId()).thenReturn(2L);when(d.estado()).thenReturn(estado);when(d.maestro()).thenReturn("Docente Ficticio");
  when(d.fechaInicio()).thenReturn(LocalDate.of(2026,10,19));when(d.fechaFin()).thenReturn(LocalDate.of(2026,10,23));
  when(d.grado()).thenReturn("Primero");when(d.grupo()).thenReturn("A");when(d.plantel()).thenReturn("Plantel ficticio");when(d.proposito()).thenReturn("Resolver sumas sencillas.");
  when(d.materias()).thenReturn(List.of(new PlaneacionDocumento.Materia(3L,"MAT","Matemáticas")));
  when(d.actividades()).thenReturn(List.of(new PlaneacionDocumento.Actividad(3L,"Matemáticas",LocalDate.of(2026,10,19),"Sumar fichas","Repasar","Sumar","Compartir",30,null,null)));
  when(servicio.detalleMaestro(any(),eq(22L))).thenReturn(d);when(servicio.versionEntidad(22L)).thenReturn(2L);return d;
 }
 @Test void detalleYEditarCompartenCapturaPdfEnvioYGestionSinFormulariosAnidados()throws Exception{
  try{documento("Borrador");var client=mvc();
   for(String ruta:List.of("/maestros/planeaciones/22","/maestros/planeaciones/22/editar")){
    var result=client.perform(get(ruta)).andReturn();assertThat(result.getResponse().getStatus()).isEqualTo(200);
    String html=org.unbescape.html.HtmlEscape.unescapeHtml(result.getResponse().getContentAsString());
    assertThat(html).contains("data-plan-view=\"captura\"","data-plan-view=\"pdf\"","data-plan-view=\"resumen\"","data-plan-view=\"gestion\"","Enviar a revisión","Abrir PDF en otra pestaña","target=\"_blank\"","href=\"/maestros/planeaciones/22/pdf\"","src=\"/maestros/fotografia\"","href=\"/maestros\"");
    assertThat(html).contains("data-plan-send-dialog","Confirmar envío","data-plan-send-cancel","19/10/2026 al 23/10/2026").doesNotContain("¿Necesitas descartarla?");
    var tags=java.util.regex.Pattern.compile("<(/?)form\\b").matcher(html.substring(html.indexOf("<main")));int depth=0,count=0;
    while(tags.find()){if(tags.group(1).isEmpty()){depth++;count++;assertThat(depth).isEqualTo(1);}else depth--;}
    assertThat(depth).isZero();assertThat(count).isEqualTo(3);
    assertThat(result.getModelAndView().getModel().get("panelPlaneacion")).isEqualTo(ruta.endsWith("editar")?"captura":"resumen");
    assertThat(html).contains("data-planeacion-confirm=\"descartar\"","data-planeacion-confirm=\"guardar\"","data-planeacion-confirm-dialog");
    fixture(ruta.endsWith("editar")?"edicion-unificada.html":"detalle-unificado.html",result.getResponse().getContentAsString());
   }
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void enviadaNoOfreceEdicionNiAccionesDeBorrador()throws Exception{
  try{documento("Enviada");var client=mvc();String html=client.perform(get("/maestros/planeaciones/22")).andReturn().getResponse().getContentAsString();
   assertThat(html).contains("data-plan-view=\"pdf\"","data-plan-view=\"resumen\"").doesNotContain("data-plan-view=\"captura\"","data-plan-view=\"gestion\"","Enviar a revisión","data-planeacion-form","data-plan-send-dialog");
   assertThat(client.perform(get("/maestros/planeaciones/22/editar")).andReturn().getResponse().getRedirectedUrl()).isEqualTo("/maestros/planeaciones/22");
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void errorAlActualizarConservaCapturaYVistaPdfSinGuardar()throws Exception{
  try{documento("Borrador");var result=mvc().perform(post("/maestros/planeaciones/22").param("proposito","Captura que se conserva")).andReturn();
   assertThat(result.getResponse().getContentAsString()).contains("Captura que se conserva","data-plan-view=\"pdf\"","data-plan-error-summary","data-pending-save=\"true\"");
   assertThat(result.getModelAndView().getModel().get("panelPlaneacion")).isEqualTo("captura");verify(servicio,never()).actualizar(any(),any(),any());
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void correccionesDestacadasAparecenEnDetalleEdicionYErroresSinInterpretarHtml()throws Exception{
  try{documento("Requiere ajustes");when(servicio.observacionRevision(any(),eq(22L))).thenReturn(new ObservacionRevisionPlaneacion("Correcciones solicitadas por administración","Agregar cómo se evaluará la actividad.\nIndicar evidencia. <script>alert(1)</script>",java.time.LocalDateTime.of(2026,10,19,16,0)));
   var client=mvc();
   for(String ruta:List.of("/maestros/planeaciones/22","/maestros/planeaciones/22/editar")){
    String html=client.perform(get(ruta)).andReturn().getResponse().getContentAsString();
    assertThat(html).contains("plan-review-feedback","Agregar cómo se evaluará la actividad.","Indicar evidencia.","&lt;script&gt;","19/10/2026 16:00").doesNotContain("<script>alert(1)</script>","data-plan-open-capture","Ir a captura →");
    assertThat(html.indexOf("plan-review-feedback")).isLessThan(html.indexOf("plan-workspace-nav"));
    fixture("correcciones.html",html);
   }
   String error=client.perform(post("/maestros/planeaciones/22").param("proposito","Mi corrección pendiente")).andReturn().getResponse().getContentAsString();
   assertThat(org.unbescape.html.HtmlEscape.unescapeHtml(error)).contains("Mi corrección pendiente","Agregar cómo se evaluará la actividad.","plan-review-feedback");verify(servicio,never()).actualizar(any(),any(),any());
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void nuevaTienePestanasCamposRequeridosYFotografiaEnCabecera()throws Exception{
  try{var response=mvc().perform(get("/maestros/planeaciones/nueva").param("inicio","2026-10-19")).andReturn().getResponse();
   assertThat(response.getStatus()).isEqualTo(200);String html=response.getContentAsString();
   assertThat(html).contains("data-plan-tab=\"actividades\"","data-plan-error-summary","/css/planeacion-form.css","/js/planeacion-form-ui.js","src=\"/maestros/fotografia\"","name=\"proposito\"","required","Guardar planeación");
   assertThat(html).contains("data-planeacion-confirm=\"guardar\"","data-planeacion-confirm-dialog","/js/planeacion-confirmacion.js");fixture("nueva.html",html);
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void postIncompletoMuestraErroresAnidadosYNoGuardaNiPierdeCaptura()throws Exception{
  try{var result=mvc().perform(post("/maestros/planeaciones").param("grupoId","2").param("fechaInicio","2026-10-19").param("fechaFin","2026-10-23").param("materiaIds","3")
    .param("proposito","Texto que se conserva").param("alineaciones[0].campoFormativo","").param("alineaciones[0].contenido","").param("alineaciones[0].procesoDesarrollo","")
    .param("actividades[0].fecha","2026-10-19").param("actividades[0].titulo","").param("actividades[0].inicio","").param("actividades[0].desarrollo","").param("actividades[0].cierre","")).andReturn();
   assertThat(result.getResponse().getStatus()).isEqualTo(200);String html=org.unbescape.html.HtmlEscape.unescapeHtml(result.getResponse().getContentAsString());
   assertThat(html).contains("Texto que se conserva","Selecciona la materia de esta actividad.","Escribe el título de esta actividad.","Describe el inicio de esta actividad.","Describe el desarrollo de esta actividad.","Describe el cierre de esta actividad.","Indica el campo formativo","plan-invalid","src=\"/maestros/fotografia\"");
   verify(servicio,never()).crear(any(),any());fixture("errores.html",result.getResponse().getContentAsString());
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void postSinActividadesNiMateriasExplicaRequisitos()throws Exception{
  try{String html=org.unbescape.html.HtmlEscape.unescapeHtml(mvc().perform(post("/maestros/planeaciones")).andReturn().getResponse().getContentAsString());
   assertThat(html).contains("Selecciona un grupo.","Indica la fecha inicial","Selecciona al menos una materia.","Agrega al menos una actividad.","Describe el propósito");verify(servicio,never()).crear(any(),any());
  }finally{SecurityContextHolder.clearContext();}
 }
 @Test void borradorCompletoSinAlineacionOpcionalSeEnviaUnaSolaVez()throws Exception{
  try{when(servicio.crear(any(),any())).thenReturn(22L);
   var response=mvc().perform(post("/maestros/planeaciones").param("grupoId","2").param("fechaInicio","2026-10-19").param("fechaFin","2026-10-23").param("materiaIds","3").param("proposito","Resolver sumas sencillas.")
    .param("actividades[0].fecha","2026-10-19").param("actividades[0].materiaId","3").param("actividades[0].titulo","Sumas con fichas").param("actividades[0].inicio","Repasar números.").param("actividades[0].desarrollo","Sumar fichas.").param("actividades[0].cierre","Compartir resultados.")).andReturn().getResponse();
   assertThat(response.getRedirectedUrl()).isEqualTo("/maestros/planeaciones/22");
   var request=org.mockito.ArgumentCaptor.forClass(PlaneacionRequest.class);verify(servicio,times(1)).crear(any(),request.capture());assertThat(request.getValue().alineaciones()).isEmpty();assertThat(request.getValue().actividades()).hasSize(1);
  }finally{SecurityContextHolder.clearContext();}
 }
}
