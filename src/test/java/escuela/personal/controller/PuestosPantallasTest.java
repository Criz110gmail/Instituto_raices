package escuela.personal.controller;
import escuela.personal.service.*;
import escuela.personal.dto.*;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.common.exception.ReglaNegocioException;
import org.junit.jupiter.api.*;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.util.*;
import java.nio.file.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

class PuestosPantallasTest {
 final CondicionesSalarialesService service=mock(CondicionesSalarialesService.class);
 final EmpleadoService empleados=mock(EmpleadoService.class);
 final ExcelSalarialService excel=mock(ExcelSalarialService.class);
 final CondicionesSalarialesController controller=new CondicionesSalarialesController(service,empleados,excel);
 MockMvc mvc;
 @BeforeEach void preparar(){
  var permisos=List.of("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR").stream().map(SimpleGrantedAuthority::new).toList();
  var p=new UsuarioPrincipal(1L,1L,Set.of(),true,false,"qa.puestos","",permisos);
  when(empleados.autorizarSalarios(anyBoolean())).thenReturn(p);when(empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR")).thenReturn(true);
  var puesto=new CondicionesSalarialesService.Puesto(7L,"DOC-QA","Docente de prueba","Descripción ficticia",true,2L);
  when(service.puesto(7L)).thenReturn(puesto);
  when(service.puestos(anyString(),anyString(),anyInt(),anyInt())).thenReturn(new PageImpl<>(List.of(puesto),PageRequest.of(2,10),31));
  var resolver=new ClassLoaderTemplateResolver();resolver.setPrefix("templates/");resolver.setSuffix(".html");resolver.setCharacterEncoding("UTF-8");
  var engine=new SpringTemplateEngine();engine.setTemplateResolver(resolver);var views=new ThymeleafViewResolver();views.setTemplateEngine(engine);views.setCharacterEncoding("UTF-8");
  mvc=MockMvcBuilders.standaloneSetup(controller).setViewResolvers(views).build();
 }
 private void fixture(String nombre,String html)throws Exception{var dir=Path.of("target/qa-puestos-separados");Files.createDirectories(dir);Files.writeString(dir.resolve(nombre+".html"),html);}
 @Test void listadoSoloCatalogoConMenuNuevoEditarYFiltros()throws Exception{
  var r=mvc.perform(get("/admin/puestos").param("q","Docente").param("estado","ACTIVO").param("pagina","2").param("tamanio","10")).andReturn().getResponse();
  assertThat(r.getStatus()).isEqualTo(200);var html=r.getContentAsString();
  assertThat(html).contains("sidebar","Puestos laborales","Nuevo puesto","/admin/puestos/7/editar?q=Docente&amp;estado=ACTIVO&amp;pagina=2&amp;tamanio=10","/admin/puestos/excel?q=Docente&amp;estado=ACTIVO","status positivo");
  assertThat(html).doesNotContain("puesto-guardar","name=\"codigo\"","name=\"nombre\"","name=\"descripcion\"");fixture("listado",html);
 }
 @Test void altaYEdicionSeparadasConRegresoFiltradoYModal()throws Exception{
  for(String ruta:List.of("/admin/puestos/nuevo","/admin/puestos/7/editar")){
   var r=mvc.perform(get(ruta).param("q","Docente").param("estado","ACTIVO").param("pagina","2").param("tamanio","10")).andReturn().getResponse();
   assertThat(r.getStatus()).isEqualTo(200);var html=r.getContentAsString();
   assertThat(html).contains("puesto-guardar","Volver al listado","name=\"codigo\"","name=\"pagina\" value=\"2\"","/admin/puestos?q=Docente&amp;estado=ACTIVO&amp;pagina=2&amp;tamanio=10").doesNotContain("<table", "Catálogo de puestos</h2>");
   if(ruta.endsWith("editar"))assertThat(html).contains("Editar puesto laboral","value=\"DOC-QA\"","name=\"version\" value=\"2\"");
   fixture(ruta.endsWith("editar")?"editar":"nuevo",html);
  }
 }
 @Test void validacionYDuplicadoPermanecenEnFormularioSinLimpiarCaptura()throws Exception{
  var invalido=mvc.perform(post("/admin/puestos").param("codigo","").param("nombre","").param("q","Docente").param("pagina","2").param("tamanio","10")).andReturn().getResponse();
  assertThat(invalido.getStatus()).isEqualTo(200);assertThat(invalido.getContentAsString()).contains("field-error","Nuevo puesto laboral").doesNotContain("<table");verify(service,never()).guardarPuesto(any(),any());
  when(service.guardarPuesto(isNull(),any())).thenThrow(new ReglaNegocioException("Ya existe ese código de puesto."));
  var duplicado=mvc.perform(post("/admin/puestos").param("codigo","DOC-QA").param("nombre","Nombre capturado").param("q","Docente").param("estado","ACTIVO").param("pagina","2").param("tamanio","10")).andReturn().getResponse();
  assertThat(duplicado.getStatus()).isEqualTo(200);var html=duplicado.getContentAsString();assertThat(html).contains("Ya existe ese código","value=\"Nombre capturado\"","name=\"pagina\" value=\"2\"").doesNotContain("<table");fixture("errores",html);
 }
 @Test void guardarVuelveAlListadoConFiltrosYPaginaSinUrlExterna()throws Exception{
  var r=mvc.perform(post("/admin/puestos").param("id","7").param("version","2").param("codigo","DOC-QA").param("nombre","Docente de prueba").param("q","Docente de prueba").param("estado","ACTIVO").param("pagina","2").param("tamanio","10")).andReturn().getResponse();
  assertThat(r.getStatus()).isEqualTo(302);assertThat(r.getRedirectedUrl()).isEqualTo("/admin/puestos?q=Docente%20de%20prueba&estado=ACTIVO&pagina=2&tamanio=10");verify(service).guardarPuesto(eq(7L),any());
 }
 @Test void lecturaOcultaAccionesYNoPermiteAbrirFormularios()throws Exception{
  when(empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR")).thenReturn(false);
  var html=mvc.perform(get("/admin/puestos")).andReturn().getResponse().getContentAsString();assertThat(html).contains("Sólo consulta").doesNotContain("Nuevo puesto","Editar →");
  when(empleados.autorizarSalarios(true)).thenThrow(new org.springframework.security.access.AccessDeniedException("Sólo consulta"));
  assertThatThrownBy(()->controller.nuevoPuesto("","TODOS",0,25,new org.springframework.ui.ExtendedModelMap())).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  assertThatThrownBy(()->controller.editarPuesto(7L,"","TODOS",0,25,new org.springframework.ui.ExtendedModelMap())).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
 }
}
