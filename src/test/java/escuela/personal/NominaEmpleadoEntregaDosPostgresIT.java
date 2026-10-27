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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.nio.file.*;
import java.io.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Sólo base desechable explícita. Nunca conexión operativa ni .env. */
@EnabledIfEnvironmentVariable(named="QA_CONCEPTOS_V2_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_conceptos_v2_qa")
class NominaEmpleadoEntregaDosPostgresIT {
 private static final long E=460000L;
 private UsernamePasswordAuthenticationToken auth(String... roles){var a=Arrays.stream(roles).map(SimpleGrantedAuthority::new).toList();return new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(E,E,Set.of(),true,false,"qa.configuracion","",a),"",a);}
 private UsernamePasswordAuthenticationToken completo(){return auth("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","CONDICION_SALARIAL_ADMINISTRAR","EMPLEADO_NOMINA_ADMINISTRAR");}
 private void sesion(){SecurityContextHolder.getContext().setAuthentication(completo());}
 private ConceptoEmpleadoForm form(ConceptoEmpleadoService s,long concepto,LocalDate fecha,String importe){var c=s.contextoConfiguracion(E,concepto);var f=new ConceptoEmpleadoForm();f.setEmpleadoId(E);f.setEmpleadoTexto(c.empleado());f.setConceptoId(concepto);f.setConceptoTexto(c.concepto());f.setConceptoVersion(c.conceptoVersion());f.setLaboralVersion(c.laboralVersion());f.setInicio(fecha);f.setImporte(new BigDecimal(importe));f.setMotivo("Configuración sintética de prueba");return f;}
 @Test void configuracionFrecuenciasHistoriaUnicaPermisosRollbackYWeb()throws Exception{
  var dir=Files.createDirectories(Path.of("target/qa-conceptos-v2"));
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_CONCEPTOS_V2_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-sintetica","--spring.security.user.name=qa-recuperacion","--spring.security.user.password=qa-sintetica","--app.archivos.directorio="+dir.toAbsolutePath())){
   var j=app.getBean(JdbcTemplate.class);j.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(460000,'QA-CONFIG','Ficticia','America/Mexico_City','MXN'),(460001,'QA-CONFIG-OTRA','Otra','America/Mexico_City','MXN')");
   j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(460000,460000,'qa.configuracion','qa@example.invalid','ADMINISTRATIVO','ACTIVO','hash-ficticio')");
   j.update("INSERT INTO empleado(id,institucion_id,codigo,nombres,primer_apellido) VALUES(460000,460000,'EMP-QA','Diana','Prueba'),(460001,460001,'EMP-OTRA','Otra','Prueba')");
   var hoy=LocalDate.now(ZoneId.of("America/Mexico_City"));var inicio=hoy.plusMonths(2).withDayOfMonth(1);
   j.update("INSERT INTO empleado_laboral(empleado_id,situacion,fecha_ingreso) VALUES(460000,'ACTIVO',?),(460001,'ACTIVO',?)",hoy.minusMonths(1),hoy);
   for(String[] c:List.of(new String[]{"TRANS","Transporte","PERCEPCION","HABITUAL"},new String[]{"DED","Deducción","DEDUCCION","HABITUAL"},new String[]{"UNICO","Horas adicionales","PERCEPCION","OCASIONAL"},new String[]{"SUELDO_BASE","Sueldo base","PERCEPCION","HABITUAL"},new String[]{"LEGADO","Bono anterior","PERCEPCION","HABITUAL"},new String[]{"CONCUR","Bono concurrente","PERCEPCION","HABITUAL"}))j.update("INSERT INTO concepto_nomina(institucion_id,codigo,nombre,naturaleza,aplicacion,creado_por_id,actualizado_por_id) VALUES(460000,?,?,?,?,460000,460000)",c[0],c[1],c[2],c[3]);
   long transporte=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='TRANS'",Long.class),ded=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='DED'",Long.class),unico=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='UNICO'",Long.class),base=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='SUELDO_BASE'",Long.class),legado=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='LEGADO'",Long.class),conc=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='CONCUR'",Long.class);
   var c=app.getBean(ConceptoEmpleadoService.class);var s=app.getBean(NominaEmpleadoService.class);sesion();
   var anterior=form(c,legado,inicio,"500");long legadoId=c.asignar(anterior);assertThat(j.queryForObject("SELECT modelo FROM concepto_empleado WHERE id=?",String.class,legadoId)).isEqualTo("LEGADO");assertThat(j.queryForObject("SELECT frecuencia_importe FROM concepto_empleado WHERE id=?",String.class,legadoId)).isNull();
   assertThat(s.buscar(E,"")).hasSize(5);assertThat(s.buscar(E,"TR")).isEmpty();assertThatThrownBy(()->c.contextoConfiguracion(E,base)).hasMessageContaining("sueldo base");
   var f=form(c,transporte,inicio,"50");long primera=s.guardar(E,f);assertThat(s.metadatos(E,primera)).containsEntry("modelo","V2").containsEntry("frecuencia_importe","MENSUAL");
   assertThatThrownBy(()->s.guardar(E,f)).hasMessageContaining("Ya existe");
   var semanal=form(c,ded,inicio,"100");semanal.setFrecuenciaImporte("SEMANAL");long semana=s.guardar(E,semanal);assertThat(s.metadatos(E,semana)).containsEntry("frecuencia_importe","SEMANAL");
   var cambio=form(c,transporte,inicio.plusMonths(1),"80");cambio.setAnteriorId(primera);cambio.setAnteriorVersion(c.obtener(primera).version());
   j.execute("CREATE FUNCTION qa_falla_config() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.importe=80 THEN RAISE EXCEPTION 'Fallo sintético'; END IF; RETURN NEW; END $$");j.execute("CREATE TRIGGER qa_falla_config BEFORE INSERT ON concepto_empleado FOR EACH ROW EXECUTE FUNCTION qa_falla_config()");
   assertThatThrownBy(()->s.guardar(E,cambio)).isInstanceOf(RuntimeException.class);assertThat(c.obtener(primera).fin()).isNull();j.execute("DROP TRIGGER qa_falla_config ON concepto_empleado");long segunda=s.guardar(E,cambio);assertThat(c.obtener(primera).fin()).isEqualTo(inicio.plusMonths(1).minusDays(1));assertThat(c.obtener(primera).importe()).isEqualByComparingTo("50");assertThat(c.obtener(segunda).importe()).isEqualByComparingTo("80");
   assertThatThrownBy(()->s.guardar(E,cambio)).hasMessageContaining("anterior cambió");s.gestionar(E,segunda,c.obtener(segunda).version(),"Cancelar programación sintética",null,true);assertThat(c.obtener(primera).fin()).isNull();
   var quincenal=form(c,conc,inicio,"75");quincenal.setFrecuenciaImporte("QUINCENAL");long quincena=s.guardar(E,quincenal);assertThat(s.metadatos(E,quincena)).containsEntry("frecuencia_importe","QUINCENAL");
   j.update("INSERT INTO calendario_nomina(id,institucion_id,codigo,nombre,frecuencia,creado_por_id,actualizado_por_id) VALUES(460000,460000,'QA-MES','Mensual','MENSUAL',460000,460000)");
   j.update("INSERT INTO periodo_nomina(id,institucion_id,calendario_id,codigo,nombre,calendario_codigo,calendario_nombre,frecuencia,calendario_version,fecha_inicio,fecha_fin,fecha_pago,motivo_registro,motivo_operacion,creado_por_id,actualizado_por_id) VALUES(460000,460000,460000,'QA-PERIODO','Periodo ficticio','QA-MES','Mensual','MENSUAL',0,?,?,?,'Prueba','Prueba',460000,460000)",inicio,inicio.withDayOfMonth(inicio.lengthOfMonth()),inicio.withDayOfMonth(inicio.lengthOfMonth()));
   var per=s.periodo(E,460000L);var unica=form(c,unico,per.inicio(),"250");unica.setFin(per.fin());unica.setModalidad("UNICA");unica.setPeriodoId(per.id());unica.setPeriodoTexto(per.texto());unica.setPeriodoVersion(per.version());
   unica.setPeriodoVersion(999L);assertThatThrownBy(()->s.guardar(E,unica)).hasMessageContaining("periodo cambió");unica.setPeriodoVersion(per.version());
   unica.setInicio(per.inicio().plusDays(1));assertThatThrownBy(()->s.guardar(E,unica)).hasMessageContaining("coincidir");unica.setInicio(per.inicio());
   var invalida=form(c,unico,per.inicio(),"250");invalida.setModalidad("UNICA");assertThatThrownBy(()->s.guardar(E,invalida)).hasMessageContaining("periodo");long una=s.guardar(E,unica);assertThat(s.metadatos(E,una)).containsEntry("frecuencia_importe","UNICA").containsEntry("periodo_codigo","QA-PERIODO");
   assertThatThrownBy(()->s.gestionar(E,una,0L,"No recortar periodo",hoy,false)).hasMessageContaining("único");
   // Terminar conserva fecha inclusiva e historial; esta fila no se usa en pagos.
   j.update("INSERT INTO concepto_nomina(institucion_id,codigo,nombre,naturaleza,aplicacion,creado_por_id,actualizado_por_id) VALUES(460000,'ACTUAL','Concepto actual','PERCEPCION','HABITUAL',460000,460000)");long actual=j.queryForObject("SELECT id FROM concepto_nomina WHERE codigo='ACTUAL'",Long.class);var termina=form(c,actual,hoy,"10");termina.setFin(inicio.minusDays(1));long vigente=s.guardar(E,termina);s.gestionar(E,vigente,0L,"Terminar a fecha efectiva",hoy,false);assertThat(c.obtener(vigente).fin()).isEqualTo(hoy);
   assertThatThrownBy(()->j.update("UPDATE concepto_empleado SET frecuencia_importe='SEMANAL' WHERE id=?",primera)).isInstanceOf(org.springframework.dao.DataAccessException.class);
   assertThatThrownBy(()->j.update("DELETE FROM concepto_empleado WHERE id=?",primera)).isInstanceOf(org.springframework.dao.DataAccessException.class);
   var filtrado=new FiltroNominaEmpleado("Transporte","PROXIMA","PERCEPCION","MENSUAL",inicio,inicio.plusDays(2),0,10);assertThat(s.listar(E,filtrado).getTotalElements()).isEqualTo(1);
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)app).addFilters(app.getBean(FilterChainProxy.class)).build();var web=new MockHttpSession();web.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(completo()));
   for(String ruta:List.of("/admin/empleados/460000/nomina","/admin/empleados/460000/nomina/nuevo","/admin/empleados/460000/nomina/"+primera,"/admin/empleados/460000/nomina/"+primera+"/cambio","/admin/empleados/460000")){
    var r=mvc.perform(get(ruta).session(web)).andReturn().getResponse();assertThat(r.getStatus()).as(ruta).isEqualTo(200);Files.writeString(dir.resolve(ruta.endsWith("nuevo")?"nuevo.html":ruta.endsWith("cambio")?"cambio.html":ruta.endsWith("/"+primera)?"detalle.html":ruta.endsWith("nomina")?"listado.html":"empleado.html"),r.getContentAsString());
   }
   var ex=mvc.perform(get("/admin/empleados/460000/nomina/excel").session(web).param("q","Transporte").param("frecuencia","MENSUAL").param("estado","PROXIMA")).andReturn().getResponse();assertThat(ex.getStatus()).isEqualTo(200);try(var libro=new XSSFWorkbook(new ByteArrayInputStream(ex.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);assertThat(libro.getSheetAt(0).getRow(1).getCell(3).getNumericCellValue()).isEqualTo(50);}
   var nuevo=mvc.perform(get("/admin/empleados/460000/nomina/nuevo").session(web)).andReturn();var csrf=(org.springframework.security.web.csrf.CsrfToken)nuevo.getRequest().getAttribute(org.springframework.security.web.csrf.CsrfToken.class.getName());
   sesion();var postForm=form(c,transporte,inicio.plusMonths(2),"90");postForm.setAnteriorId(primera);postForm.setAnteriorVersion(c.obtener(primera).version());
   var postOk=post("/admin/empleados/460000/nomina").session(web).param(csrf.getParameterName(),csrf.getToken()).param("empleadoId",Long.toString(E)).param("empleadoTexto",postForm.getEmpleadoTexto()).param("conceptoId",Long.toString(transporte)).param("conceptoTexto",postForm.getConceptoTexto()).param("conceptoVersion",postForm.getConceptoVersion().toString()).param("laboralVersion",postForm.getLaboralVersion().toString()).param("importe","90").param("inicio",postForm.getInicio().toString()).param("motivo","Cambio desde POST real").param("anteriorId",Long.toString(primera)).param("anteriorVersion",postForm.getAnteriorVersion().toString()).param("frecuenciaImporte","QUINCENAL").param("modalidad","RECURRENTE").param("q","Transporte").param("estado","PROXIMA");
   var creada=mvc.perform(postOk).andReturn().getResponse();assertThat(creada.getStatus()).isEqualTo(302);assertThat(creada.getRedirectedUrl()).contains("q=Transporte","estado=PROXIMA");assertThat(mvc.perform(postOk).andReturn().getResponse().getContentAsString()).contains("anterior cambi");sesion();
   var mal=mvc.perform(post("/admin/empleados/460000/nomina").session(web).param(csrf.getParameterName(),csrf.getToken()).param("empleadoId","460000").param("importe","-1").param("motivo","Conservar captura inválida")).andReturn().getResponse();assertThat(mal.getStatus()).isEqualTo(200);assertThat(mal.getContentAsString()).contains("Conservar captura inv&aacute;lida","field-error");
   assertThat(mvc.perform(post("/admin/empleados/460000/nomina").session(web)).andReturn().getResponse().getStatus()).isBetween(300,499);
   var lectura=auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER","EMPLEADO_NOMINA_LEER");web.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(lectura));assertThat(mvc.perform(get("/admin/empleados/460000/nomina").session(web)).andReturn().getResponse().getStatus()).isEqualTo(200);assertThat(mvc.perform(get("/admin/empleados/460000/nomina/nuevo").session(web)).andReturn().getResponse().getStatus()).isBetween(300,499);
   web.setAttribute("SPRING_SECURITY_CONTEXT",new SecurityContextImpl(auth("EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_LEER")));assertThat(mvc.perform(get("/admin/empleados/460000/nomina/excel").session(web)).andReturn().getResponse().getStatus()).isBetween(300,499);var privado=mvc.perform(get("/admin/empleados/460000").session(web)).andReturn().getResponse().getContentAsString();assertThat(privado).doesNotContain("tab-emp-nomina","50.00","Bono anterior");
   sesion();assertThatThrownBy(()->s.listar(460001L,FiltroNominaEmpleado.inicial())).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   var prep=app.getBean(PreparacionNominaService.class);var candidatos=prep.candidatos(460000L,new FiltroNomina("","",null,"",null,"",E,"",null,null,0,25),"REVISION");assertThat(candidatos.getTotalElements()).isEqualTo(1);assertThat(candidatos.getContent().getFirst().motivo()).contains("motor proporcional");assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();assertThat(j.queryForObject("SELECT count(*) FROM nomina_borrador",Long.class)).isZero();assertThat(c.historial(primera,0).getTotalElements()).isGreaterThanOrEqualTo(3);
  }finally{SecurityContextHolder.clearContext();}
 }
}
