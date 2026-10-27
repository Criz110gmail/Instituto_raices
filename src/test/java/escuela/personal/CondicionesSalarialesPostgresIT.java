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
import java.io.*;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Sólo instancia desechable explícita, sin lectura de .env ni uso de datos reales. */
@EnabledIfEnvironmentVariable(named="QA_SALARIOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_salarios_qa")
class CondicionesSalarialesPostgresIT {
 private UsernamePasswordAuthenticationToken auth(String... permisos){var a=Arrays.stream(permisos).map(SimpleGrantedAuthority::new).toList();var p=new UsuarioPrincipal(95000L,95000L,Set.of(),true,false,"qa.salarios","",a);return new UsernamePasswordAuthenticationToken(p,"",a);}
 private UsernamePasswordAuthenticationToken completo(){return auth("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","CONDICION_SALARIAL_ADMINISTRAR");}
 private CondicionSalarialForm form(CondicionesSalarialesService s,EmpleadoService empleados,Long puesto,LocalDate inicio,String monto){var f=new CondicionSalarialForm();f.setPuestoId(puesto);var p=s.puesto(puesto);f.setPuestoTexto(p.codigo()+" · "+p.nombre());f.setImporteBruto(new BigDecimal(monto));f.setFechaInicio(inicio);f.setMotivo("Condición ficticia de QA");f.setVersionLaboral(empleados.laboral(95000L).getVersion());f.setRevision(s.revision(95000L));return f;}
 @Test void vigenciasRollbackConcurrenciaPrivacidadExcelYCeroMovimientos()throws Exception{
  var storage=java.nio.file.Files.createTempDirectory(java.nio.file.Path.of("target"),"qa-salarios-").toAbsolutePath();
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_SALARIOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-no-usada","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-no-usada","--app.archivos.directorio="+storage)){
   var jdbc=app.getBean(JdbcTemplate.class);jdbc.update("insert into institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) values(95000,'QA-SAL','Institución salarial ficticia','America/Mexico_City','MXN'),(95001,'QA-OTRA','Otra institución','America/Mexico_City','MXN')");
   jdbc.update("insert into usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) values(95000,95000,'qa.salarios','qa@example.invalid','ADMINISTRATIVO','ACTIVO','hash-ficticio-no-usado')");
   jdbc.update("insert into empleado(id,institucion_id,codigo,nombres,primer_apellido) values(95000,95000,'EMP-SAL-QA','Ana','Prueba'),(95001,95001,'EMP-AJENO','Otro','Empleado')");
   var hoy=LocalDate.now(ZoneId.of("America/Mexico_City"));
   jdbc.update("insert into empleado_laboral(empleado_id,puesto,fecha_ingreso,situacion) values(95000,'Referencia previa',?,'ACTIVO'),(95001,NULL,NULL,'POR_CONFIRMAR')",hoy.minusDays(10));
   var s=app.getBean(CondicionesSalarialesService.class);var empleados=app.getBean(EmpleadoService.class);SecurityContextHolder.getContext().setAuthentication(completo());
   // Fixtures anteriores a V135: no inventar horas/sueldo para su compatibilidad.
   var p=new PuestoForm();p.setCodigo("DOC-QA");p.setNombre("Docente de prueba");p.setDescripcion("Sólo capacitación");
   Long puesto=jdbc.queryForObject("INSERT INTO puesto_laboral(institucion_id,codigo,nombre,descripcion,creado_por_id,actualizado_por_id) VALUES(95000,'DOC-QA','Docente de prueba','Sólo capacitación',95000,95000) RETURNING id",Long.class);
   for(int n=0;n<12;n++)jdbc.update("INSERT INTO puesto_laboral(institucion_id,codigo,nombre,creado_por_id,actualizado_por_id) VALUES(95000,?,?,95000,95000)","P-"+n,"Puesto ficticio "+n);
   assertThat(s.buscarPuestos("")).hasSize(11);assertThat(s.buscarPuestos("do")).isEmpty();assertThat(s.buscarPuestos("doc")).hasSize(1);assertThat(s.puestos("","ACTIVO",0,10).getContent()).hasSize(10);assertThat(s.puestos("","ACTIVO",1,10).getContent()).hasSize(3);
   var invalida=form(s,empleados,puesto,hoy.minusDays(1),"12000.00");assertThatThrownBy(()->s.programar(95000L,invalida)).hasMessageContaining("vigencia");
   invalida.setFechaInicio(hoy.plusDays(2));invalida.setImporteBruto(BigDecimal.ZERO);assertThatThrownBy(()->s.programar(95000L,invalida)).hasMessageContaining("positivo");
   invalida.setImporteBruto(new BigDecimal("12000.00"));invalida.setFechaFin(hoy);assertThatThrownBy(()->s.programar(95000L,invalida)).hasMessageContaining("fin de vigencia");
   jdbc.update("update empleado_laboral set situacion='BAJA',fecha_baja=?,motivo_baja='Sólo QA',version=version+1 where empleado_id=95000",hoy);
   assertThatThrownBy(()->s.programar(95000L,invalida)).hasMessageContaining("en baja");
   jdbc.update("update empleado_laboral set situacion='ACTIVO',fecha_baja=NULL,motivo_baja=NULL,version=version+1 where empleado_id=95000");
   var inicial=form(s,empleados,puesto,hoy.plusDays(2),"12000.00");s.programar(95000L,inicial);
   var primero=s.aplicable(95000L,inicial.getFechaInicio());assertThat(primero.importeBruto()).isEqualByComparingTo("12000");assertThat(s.aplicable(95000L,hoy)).isNull();
   assertThatThrownBy(()->s.programar(95000L,inicial)).hasMessageContaining("cambiaron");
   // Error después de cerrar la anterior: todo el cambio y su historial deben revertirse.
   var nuevo=form(s,empleados,puesto,hoy.plusDays(60),"13000.00");
   jdbc.execute("CREATE FUNCTION qa_falla_sueldo() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.importe_bruto=13000 THEN RAISE EXCEPTION 'Falla deliberada'; END IF; RETURN NEW; END $$");jdbc.execute("CREATE TRIGGER qa_falla BEFORE INSERT ON condicion_salarial FOR EACH ROW EXECUTE FUNCTION qa_falla_sueldo()");
   long revision=s.revision(95000L);assertThatThrownBy(()->s.programar(95000L,nuevo)).isInstanceOf(RuntimeException.class);assertThat(s.revision(95000L)).isEqualTo(revision);assertThat(s.aplicable(95000L,hoy.plusDays(90)).fin()).isNull();jdbc.execute("DROP TRIGGER qa_falla ON condicion_salarial");
   // Dos confirmaciones de la misma propuesta: sólo una registra y cierra la anterior.
   var pool=Executors.newFixedThreadPool(2);var inicio=new CountDownLatch(1);
   try{Callable<Boolean> tarea=()->{SecurityContextHolder.getContext().setAuthentication(completo());try{inicio.await();s.programar(95000L,nuevo);return true;}catch(RuntimeException e){return false;}finally{SecurityContextHolder.clearContext();}};var uno=pool.submit(tarea);var dos=pool.submit(tarea);inicio.countDown();assertThat(List.of(uno.get(30,TimeUnit.SECONDS),dos.get(30,TimeUnit.SECONDS))).containsExactlyInAnyOrder(true,false);}finally{pool.shutdownNow();}
   SecurityContextHolder.getContext().setAuthentication(completo());
   assertThat(s.aplicable(95000L,hoy.plusDays(59)).fin()).isEqualTo(hoy.plusDays(59));var segundo=s.aplicable(95000L,hoy.plusDays(60));assertThat(segundo.importeBruto()).isEqualByComparingTo("13000");
   // El puesto renombrado no altera su fotografía histórica.
   p.setCodigo("DOC-REN");p.setNombre("Nombre corregido");p.setVersion(0L);p.setHorasSemanales(new BigDecimal("40"));p.setSueldoMensual(new BigDecimal("12000"));s.guardarPuesto(puesto,p);assertThat(s.aplicable(95000L,hoy.plusDays(60)).puestoNombre()).isEqualTo("Docente de prueba");
   var seleccionadoViejo=form(s,empleados,puesto,hoy.plusDays(90),"14000.00");seleccionadoViejo.setPuestoTexto("DOC-QA · Docente de prueba");assertThatThrownBy(()->s.programar(95000L,seleccionadoViejo)).hasMessageContaining("puesto cambió");
   var filtro=new FiltroSalarial("Docente","PROXIMA",hoy.plusDays(60),hoy.plusDays(100),0,10);
   assertThat(s.listar(95000L,filtro).getTotalElements()).isEqualTo(1);
   var salida=new ByteArrayOutputStream();app.getBean(ExcelSalarialService.class).condiciones(95000L,filtro,salida);try(var libro=new XSSFWorkbook(new ByteArrayInputStream(salida.toByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(4).getNumericCellValue()).isEqualTo(13000);}
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var session=new MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(completo()));
   var pagina=mvc.perform(get("/admin/empleados/95000/condiciones").session(session)).andReturn().getResponse();assertThat(pagina.getStatus()).isEqualTo(200);assertThat(pagina.getContentAsString()).contains("Registrar condición","DOC-QA","data-money-confirm=\"salarial-guardar\"","data-money-confirm=\"salarial-cancelar\"","data-no-scope");
   var dir=java.nio.file.Path.of("target/qa-salarios");java.nio.file.Files.createDirectories(dir);java.nio.file.Files.writeString(dir.resolve("condiciones.html"),pagina.getContentAsString());
   var catalogo=mvc.perform(get("/admin/puestos").session(session)).andReturn().getResponse();assertThat(catalogo.getStatus()).isEqualTo(200);assertThat(catalogo.getContentAsString()).contains("Nombre corregido","Nuevo puesto").doesNotContain("puesto-guardar");java.nio.file.Files.writeString(dir.resolve("puestos.html"),catalogo.getContentAsString());
   var sinPermiso=new MockHttpSession();sinPermiso.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR")));
   var oculto=mvc.perform(get("/admin/empleados/95000").session(sinPermiso)).andReturn().getResponse();assertThat(oculto.getStatus()).isEqualTo(200);assertThat(oculto.getContentAsString()).doesNotContain("tab-emp-salarios");
   var prohibido=mvc.perform(get("/admin/empleados/95000/condiciones/excel").session(sinPermiso)).andReturn().getResponse();assertThat(prohibido.getStatus()).isBetween(300,499);assertThat(String.valueOf(prohibido.getContentType())).doesNotContain("spreadsheet");
   var lectura=new MockHttpSession();lectura.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER")));
   var soloLectura=mvc.perform(get("/admin/empleados/95000/condiciones").session(lectura)).andReturn().getResponse();assertThat(soloLectura.getStatus()).isEqualTo(200);assertThat(soloLectura.getContentAsString()).contains("12,000.00").doesNotContain("salarial-guardar","salarial-cancelar");
   var fechaFutura=mvc.perform(get("/admin/empleados/95000/condiciones").session(session).param("fechaConsulta",hoy.plusDays(60).toString())).andReturn().getResponse();assertThat(fechaFutura.getStatus()).isEqualTo(200);assertThat(fechaFutura.getContentAsString()).contains("salarial-amount","13,000.00");java.nio.file.Files.writeString(dir.resolve("condiciones.html"),fechaFutura.getContentAsString());
   SecurityContextHolder.getContext().setAuthentication(completo());long antes=s.revision(95000L);var sinCsrf=mvc.perform(post("/admin/empleados/95000/condiciones").session(session)).andReturn().getResponse();assertThat(sinCsrf.getStatus()).isBetween(300,499);SecurityContextHolder.getContext().setAuthentication(completo());assertThat(s.revision(95000L)).isEqualTo(antes);
   SecurityContextHolder.getContext().setAuthentication(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR"));assertThatThrownBy(()->s.listar(95000L,filtro)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   SecurityContextHolder.getContext().setAuthentication(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER"));assertThat(s.aplicable(95000L,hoy.plusDays(60))).isNotNull();assertThatThrownBy(()->s.programar(95000L,nuevo)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   SecurityContextHolder.getContext().setAuthentication(completo());assertThatThrownBy(()->s.aplicable(95001L,hoy)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   s.cancelar(95000L,segundo.id(),segundo.version(),"Cancelar aumento ficticio");assertThat(s.aplicable(95000L,hoy.plusDays(90)).importeBruto()).isEqualByComparingTo("12000");assertThat(s.aplicable(95000L,hoy.plusDays(90)).fin()).isNull();
   assertThatThrownBy(()->s.cancelar(95000L,segundo.id(),segundo.version(),"Repetir")).hasMessageContaining("cambió");
   assertThatThrownBy(()->jdbc.update("update condicion_salarial set importe_bruto=1 where id=?",primero.id())).isInstanceOf(org.springframework.dao.DataAccessException.class);
   assertThatThrownBy(()->jdbc.update("delete from personal_salarial_historial where empleado_id=95000")).isInstanceOf(org.springframework.dao.DataAccessException.class);
   assertThat(jdbc.queryForObject("select count(*) from movimiento_financiero",Long.class)).isZero();assertThat(jdbc.queryForObject("select puesto from empleado_laboral where empleado_id=95000",String.class)).isEqualTo("Referencia previa");
   assertThat(jdbc.queryForObject("select count(*) from rol_permiso rp join permiso p on p.id=rp.permiso_id where p.codigo like 'CONDICION_SALARIAL_%'",Long.class)).isZero();
  }finally{SecurityContextHolder.clearContext();}
 }
}
