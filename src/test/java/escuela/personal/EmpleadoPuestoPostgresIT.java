package escuela.personal;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.personal.dto.*;
import escuela.personal.service.EmpleadoService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.*;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@EnabledIfEnvironmentVariable(named="QA_SALARIOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_salarios_qa")
class EmpleadoPuestoPostgresIT {
 private UsernamePasswordAuthenticationToken auth(String... permisos){var a=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(98000L,98000L,Set.of(),true,false,"qa.puestos.empleado","",a),"",a);}
 @Test void catalogoRealCapturaValidadaSinSalariosConservaHistorialYCsrf()throws Exception{
  var storage=Files.createTempDirectory(Path.of("target"),"qa-puestos-empleado-").toAbsolutePath();
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_SALARIOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-no-usada","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-no-usada","--app.archivos.directorio="+storage)){
   var jdbc=app.getBean(JdbcTemplate.class);
   jdbc.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(98000,'QA-PUESTO-EMP','Institución ficticia','America/Mexico_City','MXN'),(98001,'QA-PUESTO-OTRA','Otra ficticia','America/Mexico_City','MXN')");
   jdbc.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(98000,98000,'qa.puestos.empleado','qa.puestos@example.invalid','ADMINISTRATIVO','ACTIVO','no-usado')");
   var auth=auth("EMPLEADO_ADMINISTRAR","EMPLEADO_LABORAL_ADMINISTRAR");SecurityContextHolder.getContext().setAuthentication(auth);
   var service=app.getBean(EmpleadoService.class);var f=new EmpleadoForm();f.setInstitucionId(98000L);f.setCodigo("EMP-QA");f.setNombres("Persona");f.setPrimerApellido("Ficticia");Long id=service.guardar(null,f);
   for(int n=0;n<12;n++)jdbc.update("INSERT INTO puesto_laboral(id,institucion_id,codigo,nombre,activo,creado_por_id,actualizado_por_id) VALUES(?,98000,?,?,true,98000,98000)",98000+n,"QA-PUESTO-"+n,"Puesto ficticio "+n);
   jdbc.update("INSERT INTO puesto_laboral(id,institucion_id,codigo,nombre,activo,creado_por_id,actualizado_por_id) VALUES(98100,98000,'QA-INACTIVO','Inactivo ficticio',false,98000,98000),(98101,98001,'QA-AJENO','Ajeno ficticio',true,98000,98000)");
   assertThat(service.buscarPuestosLaborales(id,"")).hasSize(11);assertThat(service.buscarPuestosLaborales(id,"Pu")).isEmpty();
   assertThat(service.buscarPuestosLaborales(id,"QA-PUESTO-0")).hasSize(1);assertThat(service.buscarPuestosLaborales(id,"Inactivo")).isEmpty();assertThat(service.buscarPuestosLaborales(id,"Ajeno")).isEmpty();assertThat(service.buscarPuestosLaborales(id,"%%%_")).isEmpty();
   var laboral=service.laboral(id);laboral.setSituacion("ACTIVO");laboral.setFechaIngreso(LocalDate.of(2026,1,1));laboral.setPuesto("Puesto ficticio 0");
   assertThatThrownBy(()->service.guardarLaboral(id,laboral)).hasMessageContaining("sugerencias");
   laboral.setPuestoId(98101L);assertThatThrownBy(()->service.guardarLaboral(id,laboral)).hasMessageContaining("activo de tu institución");
   laboral.setPuestoId(98100L);assertThatThrownBy(()->service.guardarLaboral(id,laboral)).hasMessageContaining("activo de tu institución");
   laboral.setPuestoId(98000L);service.guardarLaboral(id,laboral);assertThat(service.laboral(id).getPuesto()).isEqualTo("Puesto ficticio 0");
   long historial=jdbc.queryForObject("SELECT count(*) FROM empleado_laboral_historial WHERE empleado_id=?",Long.class,id);
   jdbc.update("UPDATE puesto_laboral SET nombre='Nombre posterior',activo=false WHERE id=98000");
   assertThat(service.laboral(id).getPuesto()).isEqualTo("Puesto ficticio 0");
   var legacy=service.laboral(id);legacy.setPuestoId(null);service.guardarLaboral(id,legacy);assertThat(service.laboral(id).getPuesto()).isEqualTo("Puesto ficticio 0");
   assertThat(jdbc.queryForObject("SELECT count(*) FROM condicion_salarial",Long.class)).isZero();assertThat(jdbc.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
   assertThat(jdbc.queryForObject("SELECT count(*) FROM empleado_laboral_historial WHERE empleado_id=?",Long.class,id)).isGreaterThanOrEqualTo(historial);
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth));
   var get=mvc.perform(get("/admin/empleados/"+id).session(session)).andReturn();assertThat(get.getResponse().getStatus()).isEqualTo(200);assertThat(get.getResponse().getContentAsString()).contains("laboral-puesto","Puesto ficticio 0","name=\"puestoId\"");
   var csrf=(CsrfToken)get.getRequest().getAttribute("_csrf");
   var ayuda=mvc.perform(get("/admin/empleados/"+id+"/puestos/buscar").session(session)).andReturn().getResponse();assertThat(ayuda.getStatus()).isEqualTo(200);assertThat(ayuda.getHeader("Cache-Control")).contains("no-store");assertThat(ayuda.getContentAsString()).doesNotContain("Nombre posterior","QA-AJENO","sueldo");
   SecurityContextHolder.getContext().setAuthentication(auth);var vigente=service.laboral(id);String ruta="/admin/empleados/"+id+"/laboral";
   assertThat(mvc.perform(post(ruta).session(session).param("version",vigente.getVersion().toString())).andReturn().getResponse().getStatus()).isEqualTo(403);
   var error=mvc.perform(post(ruta).session(session).param(csrf.getParameterName(),csrf.getToken()).param("version",vigente.getVersion().toString()).param("situacion","ACTIVO").param("fechaIngreso","2026-01-01").param("puesto","Texto no seleccionado")).andReturn().getResponse();
   assertThat(error.getStatus()).isEqualTo(200);assertThat(error.getContentAsString()).contains("Texto no seleccionado","Selecciona el puesto de las sugerencias");SecurityContextHolder.getContext().setAuthentication(auth);assertThat(service.laboral(id).getPuesto()).isEqualTo("Puesto ficticio 0");
   var ok=mvc.perform(post(ruta).session(session).param(csrf.getParameterName(),csrf.getToken()).param("version",vigente.getVersion().toString()).param("situacion","ACTIVO").param("fechaIngreso","2026-01-01").param("puestoId","98001").param("puesto","Puesto ficticio 1")).andReturn().getResponse();assertThat(ok.getStatus()).isEqualTo(302);SecurityContextHolder.getContext().setAuthentication(auth);assertThat(service.laboral(id).getPuesto()).isEqualTo("Puesto ficticio 1");
   SecurityContextHolder.getContext().setAuthentication(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER"));assertThatThrownBy(()->service.buscarPuestosLaborales(id,"")).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  }finally{SecurityContextHolder.clearContext();}
 }
}
