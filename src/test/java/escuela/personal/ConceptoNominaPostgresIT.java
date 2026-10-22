package escuela.personal;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnabledIfEnvironmentVariable(named="QA_SALARIOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_salarios_qa")
class ConceptoNominaPostgresIT {
 private UsernamePasswordAuthenticationToken auth(Long usuario,Long institucion,boolean institucional,boolean recuperacion,String... permisos){var a=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(usuario,institucion,Set.of(),institucional,recuperacion,"qa.conceptos","",a),"",a);}
 private UsernamePasswordAuthenticationToken completo(){return auth(97000L,97000L,true,false,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR","GUIA_PROCESOS_CONSULTAR");}
 private void sesion(){SecurityContextHolder.getContext().setAuthentication(completo());}
 private MockHttpSession web(UsernamePasswordAuthenticationToken a){var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(a));return session;}
 private ConceptoNominaForm form(String codigo,String naturaleza,String aplicacion){var f=new ConceptoNominaForm();f.setCodigo(codigo);f.setNombre("Concepto ficticio "+codigo);f.setDescripcion("Sólo capacitación; sin asignar importes");f.setNaturaleza(naturaleza);f.setAplicacion(aplicacion);return f;}
 @Test void catalogoPrivadoVersionadoPaginadoConAuditoriaRenderFiltrosExcelYCsrf()throws Exception{
  var storage=Files.createTempDirectory(Path.of("target"),"qa-conceptos-").toAbsolutePath();
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_SALARIOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-no-usada","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-no-usada","--app.archivos.directorio="+storage)){
   var jdbc=app.getBean(JdbcTemplate.class);jdbc.update("insert into institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) values(97000,'QA-NOM','Institución ficticia conceptos','America/Mexico_City','MXN'),(97001,'QA-NOM-OTRA','Otra institución ficticia','America/Mexico_City','MXN')");
   jdbc.update("insert into usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) values(97000,97000,'qa.conceptos','qa.nom@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado'),(97001,97001,'qa.otro','qa.otro@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado')");
   var s=app.getBean(ConceptoNominaService.class);sesion();var bono=form("bon-test","PERCEPCION","HABITUAL");var id=s.guardar(null,bono);assertThat(s.obtener(id).codigo()).isEqualTo("BON-TEST");
   assertThatThrownBy(()->s.guardar(null,form("BON-TEST","PERCEPCION","HABITUAL"))).hasMessageContaining("Ya existe");
   bono.setNombre("Bono actualizado");s.guardar(id,bono);assertThat(s.obtener(id).version()).isEqualTo(1L);assertThatThrownBy(()->s.guardar(id,bono)).hasMessageContaining("otra pestaña");
   var ded=form("DES-TEST","DEDUCCION","OCASIONAL");ded.setActivo(false);var desId=s.guardar(null,ded);s.guardar(null,form("PAT-TEST","APORTACION_PATRONAL","HABITUAL"));
   for(int n=0;n<12;n++)s.guardar(null,form("AUX-"+n,"PERCEPCION","HABITUAL"));
   assertThat(s.buscar("")).hasSize(11);assertThat(s.buscar("BO")).isEmpty();assertThat(s.buscar("BON")).hasSize(1);assertThat(s.buscar("DES-TEST").getFirst().detalle()).contains("Inactivo");
   var filtro=new FiltroConceptoNomina("","ACTIVO","PERCEPCION","HABITUAL",id,null,0,10);assertThat(s.listar(filtro).getTotalElements()).isEqualTo(1);assertThat(s.listar(filtro.pagina(1,10))).isEmpty();
   assertThat(s.listar(new FiltroConceptoNomina("","INACTIVO","DEDUCCION","OCASIONAL",null,null,0,25)).getContent()).extracting(ConceptoNominaService.Concepto::id).containsExactly(desId);
   assertThatThrownBy(()->jdbc.update("delete from concepto_nomina where id=?",id)).isInstanceOf(org.springframework.dao.DataAccessException.class);
   assertThat(jdbc.queryForObject("select count(*) from concepto_nomina_historial where concepto_id=?",Long.class,id)).isEqualTo(2);
   assertThatThrownBy(()->jdbc.update("delete from concepto_nomina_historial where concepto_id=?",id)).isInstanceOf(org.springframework.dao.DataAccessException.class);
   jdbc.update("update institucion set activo=false where id=97000");assertThatThrownBy(()->s.guardar(null,form("NO-INACTIVA","PERCEPCION","HABITUAL"))).hasMessageContaining("inactiva");assertThat(s.obtener(id).codigo()).isEqualTo("BON-TEST");jdbc.update("update institucion set activo=true where id=97000");
   SecurityContextHolder.getContext().setAuthentication(auth(97001L,97001L,true,false,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR"));assertThat(s.buscar("")).isEmpty();assertThatThrownBy(()->s.obtener(id)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);s.guardar(null,form("BON-TEST","PERCEPCION","HABITUAL"));
   for(var a:List.of(auth(97000L,97000L,false,false,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR"),auth(97000L,97000L,true,true,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR"),auth(97000L,97000L,true,false,"EMPLEADO_LEER"))){SecurityContextHolder.getContext().setAuthentication(a);assertThatThrownBy(()->s.buscar("")).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);}
   sesion();var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=web(completo());var dir=Path.of("target/qa-conceptos-nomina");Files.createDirectories(dir);
   var guia=mvc.perform(get("/admin/guias/conceptos-nomina-catalogo-confirmado").session(session)).andReturn().getResponse();assertThat(guia.getStatus()).isEqualTo(200);assertThat(guia.getContentAsString()).contains("Conceptos de nómina: crear, filtrar y proteger cambios","los tres conceptos Activos","dos pestañas","APORT-TEST");Files.writeString(dir.resolve("guia.html"),guia.getContentAsString());
   assertThat(jdbc.queryForObject("select count(*) from guia_proceso_paso where guia_id=(select id from guia_proceso where slug='conceptos-nomina-catalogo-confirmado')",Long.class)).isEqualTo(10);
   var excelGuia=mvc.perform(get("/admin/guias/excel").session(session).param("q","APORT-TEST").param("categoria","Personal")).andReturn().getResponse();assertThat(excelGuia.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(excelGuia.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(10);}
   var soloGuias=web(auth(97000L,97000L,true,false,"GUIA_PROCESOS_CONSULTAR"));assertThat(mvc.perform(get("/admin/guias/conceptos-nomina-catalogo-confirmado").session(soloGuias)).andReturn().getResponse().getStatus()).isBetween(300,499);
   for(String ruta:List.of("/admin/conceptos-nomina","/admin/conceptos-nomina/nuevo","/admin/conceptos-nomina/"+id+"/editar")){
    var response=mvc.perform(get(ruta).session(session).param("filtroNaturaleza","PERCEPCION").param("filtroAplicacion","HABITUAL").param("estado","ACTIVO").param("conceptoId",id.toString())).andReturn().getResponse();assertThat(response.getStatus()).as(ruta).isEqualTo(200);var html=response.getContentAsString();assertThat(html).contains("Concepto","filtroNaturaleza","filtroAplicacion");
    if(ruta.equals("/admin/conceptos-nomina"))assertThat(html).contains("Bono actualizado","1 registros","filtroNaturaleza=PERCEPCION").doesNotContain("entity-form");else assertThat(html).contains("data-money-confirm=\"concepto-nomina-guardar\"","name=\"filtroNaturaleza\" value=\"PERCEPCION\"").doesNotContain("<table","name=\"importe\"");
    Files.writeString(dir.resolve(ruta.endsWith("nuevo")?"nuevo.html":ruta.endsWith("editar")?"editar.html":"listado.html"),html);
   }
   var ayuda=mvc.perform(get("/admin/conceptos-nomina/buscar").session(session)).andReturn().getResponse();var json=new tools.jackson.databind.ObjectMapper().readTree(ayuda.getContentAsString());assertThat(json.get("resultados").size()).isEqualTo(10);assertThat(json.get("hayMas").asBoolean()).isTrue();Files.writeString(dir.resolve("conceptos.json"),ayuda.getContentAsString());
   var excel=mvc.perform(get("/admin/conceptos-nomina/excel").session(session).param("filtroNaturaleza","DEDUCCION").param("filtroAplicacion","OCASIONAL").param("estado","INACTIVO")).andReturn().getResponse();assertThat(excel.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new ByteArrayInputStream(excel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(0).getStringCellValue()).isEqualTo("DES-TEST");}
   var incompleta=mvc.perform(get("/admin/conceptos-nomina").session(session).param("conceptoTexto","Texto sin seleccionar")).andReturn().getResponse();assertThat(incompleta.getContentAsString()).contains("Selecciona el concepto").doesNotContain("Bono actualizado");
   var nueva=mvc.perform(get("/admin/conceptos-nomina/nuevo").session(session)).andReturn();var csrf=(CsrfToken)nueva.getRequest().getAttribute(CsrfToken.class.getName());
   var guardado=mvc.perform(post("/admin/conceptos-nomina").session(session).param(csrf.getParameterName(),csrf.getToken()).param("codigo","POST-TEST").param("nombre","Concepto guardado ficticio").param("naturaleza","APORTACION_PATRONAL").param("aplicacion","OCASIONAL").param("activo","true").param("version","0").param("filtroNaturaleza","PERCEPCION").param("filtroAplicacion","HABITUAL").param("pagina","2").param("tamanio","10")).andReturn().getResponse();assertThat(guardado.getStatus()).isEqualTo(302);assertThat(guardado.getRedirectedUrl()).contains("filtroNaturaleza=PERCEPCION","filtroAplicacion=HABITUAL","pagina=2");
   assertThat(jdbc.queryForObject("select naturaleza||'/'||aplicacion from concepto_nomina where codigo='POST-TEST'",String.class)).isEqualTo("APORTACION_PATRONAL/OCASIONAL");
   var invalida=mvc.perform(post("/admin/conceptos-nomina").session(session).param(csrf.getParameterName(),csrf.getToken()).param("codigo","DATOS MAL").param("nombre","Captura que debe conservarse").param("filtroNaturaleza","PERCEPCION").param("version","0")).andReturn().getResponse();assertThat(invalida.getStatus()).isEqualTo(200);assertThat(invalida.getContentAsString()).contains("field-error","Captura que debe conservarse","name=\"filtroNaturaleza\" value=\"PERCEPCION\"");Files.writeString(dir.resolve("errores.html"),invalida.getContentAsString());
   assertThat(mvc.perform(post("/admin/conceptos-nomina").session(session)).andReturn().getResponse().getStatus()).isBetween(300,499);
   var lectura=web(auth(97000L,97000L,true,false,"EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER"));var htmlLectura=mvc.perform(get("/admin/conceptos-nomina").session(lectura)).andReturn().getResponse();assertThat(htmlLectura.getStatus()).isEqualTo(200);assertThat(htmlLectura.getContentAsString()).doesNotContain("＋ Nuevo concepto","Editar →");assertThat(mvc.perform(get("/admin/conceptos-nomina/nuevo").session(lectura)).andReturn().getResponse().getStatus()).isBetween(300,499);
   var sinPermiso=web(auth(97000L,97000L,true,false,"EMPLEADO_LEER"));for(String ruta:List.of("/admin/conceptos-nomina","/admin/conceptos-nomina/buscar","/admin/conceptos-nomina/excel"))assertThat(mvc.perform(get(ruta).session(sinPermiso)).andReturn().getResponse().getStatus()).isBetween(300,499);
   assertThat(jdbc.queryForObject("select count(*) from movimiento_financiero",Long.class)).isZero();assertThat(jdbc.queryForObject("select count(*) from condicion_salarial",Long.class)).isZero();
  }finally{SecurityContextHolder.clearContext();}
 }
}
