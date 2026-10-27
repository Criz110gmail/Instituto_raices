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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Sólo PostgreSQL desechable explícito; nunca la base operativa ni .env. */
@EnabledIfEnvironmentVariable(named="QA_PUESTOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_puestos_qa")
class PuestosEntregaUnoPostgresIT {
 private UsernamePasswordAuthenticationToken auth(String... permisos){var a=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(360000L,360000L,Set.of(),true,false,"qa.puestos","",a),"",a);}
 private UsernamePasswordAuthenticationToken completo(){return auth("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","CONDICION_SALARIAL_ADMINISTRAR");}
 private PuestoForm puesto(String codigo,String horas,String sueldo){var f=new PuestoForm();f.setCodigo(codigo);f.setNombre("Apoyo técnico");f.setHorasSemanales(new BigDecimal(horas));f.setSueldoMensual(new BigDecimal(sueldo));return f;}
 private CondicionSalarialForm asignacion(CondicionesSalarialesService s,EmpleadoService e,long puesto,LocalDate fecha){var f=new CondicionSalarialForm();var p=s.puesto(puesto);f.setPuestoId(puesto);f.setPuestoTexto(p.codigo()+" · "+p.nombre());f.setFechaInicio(fecha);f.setRevision(s.revision(360000L));f.setVersionLaboral(e.laboral(360000L).getVersion());f.setMotivo("Asignación ficticia de prueba");f.setImporteBruto(new BigDecimal("999999"));return f;}
 @Test void migracionAsignacionUnicaHistoriaConcurrenciaPrivacidadYExcel()throws Exception{
  var dir=Files.createTempDirectory(Path.of("target"),"qa-puestos-entrega1-").toAbsolutePath();
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_PUESTOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-no-usada","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-no-usada","--app.archivos.directorio="+dir)){
   var j=app.getBean(JdbcTemplate.class);j.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(360000,'QA-PUESTOS','Ficticia','America/Mexico_City','MXN'),(360001,'QA-OTRA','Otra','America/Mexico_City','MXN')");
   j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(360000,360000,'qa.puestos','qa@example.invalid','ADMINISTRATIVO','ACTIVO','hash-ficticio')");
   j.update("INSERT INTO empleado(id,institucion_id,codigo,nombres,primer_apellido) VALUES(360000,360000,'EMP-TEST','Empleado','Prueba'),(360001,360001,'EMP-OTRO','Otro','Prueba')");
   var hoy=LocalDate.now(ZoneId.of("America/Mexico_City"));var inicio=hoy.plusMonths(2).withDayOfMonth(1);
   j.update("INSERT INTO empleado_laboral(empleado_id,situacion,fecha_ingreso) VALUES(360000,'ACTIVO',?),(360001,'ACTIVO',?)",hoy,hoy);
   // Catálogo legado sigue sin inventar horas/sueldo; no alterar condiciones previas.
   j.update("INSERT INTO puesto_laboral(institucion_id,codigo,nombre,creado_por_id,actualizado_por_id) VALUES(360000,'LEGADO','Puesto anterior',360000,360000)");
   var s=app.getBean(CondicionesSalarialesService.class);var e=app.getBean(EmpleadoService.class);var org=app.getBean(OrganizacionLaboralService.class);SecurityContextHolder.getContext().setAuthentication(completo());
   assertThat(s.puestos("LEGADO","TODOS",0,10).getContent().getFirst().completo()).isFalse();
   assertThatThrownBy(()->s.guardarPuesto(null,puesto("INVALIDO","169","10000"))).hasMessageContaining("horas");
   long a=s.guardarPuesto(null,puesto("APOYO_40","40","10000")),b=s.guardarPuesto(null,puesto("APOYO_48","48","14000"));
   assertThat(s.buscarPuestos("")).hasSize(3);assertThat(s.buscarPuestos("AP")).isEmpty();
   var primera=asignacion(s,e,a,inicio);long c1=s.asignarPuesto(360000L,primera,s.puesto(a).version());
   assertThat(s.aplicable(360000L,inicio).importeBruto()).isEqualByComparingTo("10000");assertThat(org.asignacion(c1).horas()).isEqualByComparingTo("40");
   // Catálogo posterior no reescribe sueldo/horas ni permite importes manuales por otra ruta.
   var edit=puesto("APOYO_40","42","11000");edit.setVersion(s.puesto(a).version());s.guardarPuesto(a,edit);
   assertThat(s.aplicable(360000L,inicio).importeBruto()).isEqualByComparingTo("10000");assertThat(org.asignacion(c1).horas()).isEqualByComparingTo("40");
   var desactualizada=asignacion(s,e,a,inicio.plusDays(5));assertThatThrownBy(()->s.asignarPuesto(360000L,desactualizada,0L)).hasMessageContaining("cambió");
   assertThatThrownBy(()->s.programar(360000L,asignacion(s,e,b,inicio.plusDays(5)))).hasMessageContaining("Empleados");
   var segunda=asignacion(s,e,b,inicio.plusDays(15));
   long revisionAntes=s.revision(360000L);
   j.execute("CREATE FUNCTION qa_falla_puesto() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.puesto_id="+b+" THEN RAISE EXCEPTION 'Fallo deliberado'; END IF; RETURN NEW; END $$");
   j.execute("CREATE TRIGGER qa_falla_puesto BEFORE INSERT ON condicion_salarial FOR EACH ROW EXECUTE FUNCTION qa_falla_puesto()");
   assertThatThrownBy(()->s.asignarPuesto(360000L,segunda,s.puesto(b).version())).isInstanceOf(RuntimeException.class);
   assertThat(org.asignacion(c1).fin()).isNull();assertThat(s.revision(360000L)).isEqualTo(revisionAntes);
   j.execute("DROP TRIGGER qa_falla_puesto ON condicion_salarial");
   long c2=s.asignarPuesto(360000L,segunda,s.puesto(b).version());
   assertThat(s.aplicable(360000L,inicio.plusDays(14)).importeBruto()).isEqualByComparingTo("10000");assertThat(s.aplicable(360000L,inicio.plusDays(15)).importeBruto()).isEqualByComparingTo("14000");
   assertThat(org.asignacion(c1).fin()).isEqualTo(inicio.plusDays(14));assertThat(org.asignacion(c2).horas()).isEqualByComparingTo("48");
   assertThatThrownBy(()->s.asignarPuesto(360000L,segunda,s.puesto(b).version())).hasMessageContaining("cambiaron");
   var retro=asignacion(s,e,b,hoy.minusDays(1));assertThatThrownBy(()->s.asignarPuesto(360000L,retro,s.puesto(b).version())).hasMessageContaining("vigencia");
   assertThatThrownBy(()->s.asignarPuesto(360001L,asignacion(s,e,b,inicio.plusDays(20)),s.puesto(b).version())).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   // Cancelación futura restaura el fin anterior sin alterar importes históricos.
   s.cancelar(360000L,c2,org.asignacion(c2).version(),"Retirar programación de prueba");assertThat(s.aplicable(360000L,inicio.plusDays(20)).importeBruto()).isEqualByComparingTo("10000");
   assertThat(org.asignacion(c1).fin()).isNull();
   // Dos pestañas con igual revisión: exactamente una asigna y la otra falla.
   var f1=asignacion(s,e,b,inicio.plusDays(20));var f2=asignacion(s,e,b,inicio.plusDays(20));var executor=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);
   try{var tareas=new ArrayList<Future<Boolean>>();for(var f:List.of(f1,f2))tareas.add(executor.submit(()->{SecurityContextHolder.getContext().setAuthentication(completo());start.await();try{s.asignarPuesto(360000L,f,0L);return true;}catch(escuela.common.exception.ReglaNegocioException ex){return false;}finally{SecurityContextHolder.clearContext();}}));start.countDown();assertThat(tareas.get(0).get(30,TimeUnit.SECONDS)?1:0).isNotEqualTo(tareas.get(1).get(30,TimeUnit.SECONDS)?1:0);}finally{executor.shutdownNow();}
   SecurityContextHolder.getContext().setAuthentication(completo());
   var out=new ByteArrayOutputStream();app.getBean(ExcelSalarialService.class).puestos("APOYO_48","ACTIVO",b,out);try(var libro=new XSSFWorkbook(new ByteArrayInputStream(out.toByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(4).getNumericCellValue()).isEqualTo(48);assertThat(libro.getSheetAt(0).getRow(1).getCell(5).getNumericCellValue()).isEqualTo(14000);}
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(completo()));
   for(String ruta:List.of("/admin/puestos","/admin/puestos/"+a+"/editar","/admin/empleados/360000/puesto?fecha="+inicio,"/admin/empleados/360000/puesto/nuevo","/admin/empleados/360000")){
    var response=mvc.perform(get(ruta).session(session)).andReturn().getResponse();assertThat(response.getStatus()).as(ruta).isEqualTo(200);assertThat(response.getContentAsString()).doesNotContain("Whitelabel");Files.writeString(dir.resolve(ruta.contains("/nuevo")?"asignar.html":ruta.contains("fecha=")?"historial.html":ruta.contains("editar")?"puesto-form.html":ruta.equals("/admin/puestos")?"puestos.html":"empleado.html"),response.getContentAsString());
   }
   // POST real: CSRF, campos manipulados, retorno y repetición de formulario.
   var captura=mvc.perform(get("/admin/empleados/360000/puesto/nuevo").session(session)).andReturn();
   var csrf=(org.springframework.security.web.csrf.CsrfToken)captura.getRequest().getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName());
   SecurityContextHolder.getContext().setAuthentication(completo());long revisionWeb=s.revision(360000L);long versionWeb=e.laboral(360000L).getVersion();
   var peticion=post("/admin/empleados/360000/puesto").session(session).param(csrf.getParameterName(),csrf.getToken()).param("puestoId",Long.toString(a)).param("puestoTexto",s.puesto(a).codigo()+" · "+s.puesto(a).nombre()).param("puestoVersion",s.puesto(a).version().toString()).param("inicio",inicio.plusDays(25).toString()).param("motivo","Confirmación web ficticia").param("versionLaboral",Long.toString(versionWeb)).param("revision",Long.toString(revisionWeb)).param("importeBruto","1");
   assertThat(mvc.perform(peticion).andReturn().getResponse().getStatus()).isEqualTo(302);
   SecurityContextHolder.getContext().setAuthentication(completo());assertThat(s.aplicable(360000L,inicio.plusDays(25)).importeBruto()).isEqualByComparingTo("11000");
   var repetida=mvc.perform(peticion).andReturn().getResponse();assertThat(repetida.getStatus()).isEqualTo(200);assertThat(repetida.getContentAsString()).contains("cambiaron","web ficticia");
   var sinSalarios=auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER");session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(sinSalarios));
   assertThat(mvc.perform(get("/admin/empleados/360000/puesto/contexto?puestoId="+b).session(session)).andReturn().getResponse().getStatus()).isBetween(300,499);
   var privado=mvc.perform(get("/admin/empleados/360000").session(session)).andReturn().getResponse().getContentAsString();assertThat(privado).doesNotContain("Sueldo base asignado","14000","14,000");
   assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();assertThat(j.queryForObject("SELECT count(*) FROM nomina_borrador",Long.class)).isZero();
   assertThat(j.queryForObject("SELECT count(*) FROM personal_salarial_historial WHERE empleado_id=360000",Long.class)).isGreaterThan(3);
  }finally{SecurityContextHolder.clearContext();}
 }
}
