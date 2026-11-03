package escuela.personal;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.assertj.core.api.Assertions.*;

/** Sólo PostgreSQL temporal; nunca la base operativa ni archivos de credenciales. */
@EnabledIfEnvironmentVariable(named="QA_FLUJO_NOMINA_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_flujo_nomina_qa")
class NominaCeroPostgresIT {
 @Test void ceroIndividualYColectivoDeVeinteSinEgresosFicticios() throws Exception {
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_FLUJO_NOMINA_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-sintetica","--spring.security.user.name=qa-cero","--spring.security.user.password=qa-sintetica","--app.archivos.directorio=target/qa-cero")){
   var j=app.getBean(JdbcTemplate.class);
   j.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(480000,'QA-CERO','Ficticia','America/Mexico_City','MXN')");
   j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(480000,480000,'qa.cero','cero@example.invalid','ADMINISTRATIVO','ACTIVO','ficticio-no-utilizado')");
   var roles=List.of("EMPLEADO_LEER","EMPLEADO_LABORAL_ADMINISTRAR","CONDICION_SALARIAL_ADMINISTRAR","EMPLEADO_NOMINA_ADMINISTRAR","MOVIMIENTO_FINANCIERO_REGISTRAR","MOVIMIENTO_FINANCIERO_REVERTIR").stream().map(SimpleGrantedAuthority::new).toList();
   var auth=new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(480000L,480000L,Set.of(),true,false,"qa.cero","",roles),"",roles);
   SecurityContextHolder.getContext().setAuthentication(auth);
   var periodos=app.getBean(ProgramacionNominaService.class);var conceptos=app.getBean(ConceptoEmpleadoService.class);var nominas=app.getBean(PreparacionNominaService.class);var pagos=app.getBean(PagoEmpleadoNominaService.class);var multiple=app.getBean(PagoMultipleNominaService.class);
   long percepcion=j.queryForObject("INSERT INTO concepto_nomina(institucion_id,codigo,nombre,naturaleza,aplicacion,creado_por_id,actualizado_por_id) VALUES(480000,'PER-CERO','Percepción ficticia','PERCEPCION','HABITUAL',480000,480000) RETURNING id",Long.class);
   long deduccion=j.queryForObject("INSERT INTO concepto_nomina(institucion_id,codigo,nombre,naturaleza,aplicacion,creado_por_id,actualizado_por_id) VALUES(480000,'DED-CERO','Deducción ficticia','DEDUCCION','HABITUAL',480000,480000) RETURNING id",Long.class);
   long junio=periodo(periodos,"JUN-CERO-QA",LocalDate.of(2027,6,1),LocalDate.of(2027,6,30));
   empleado(j,480020);concepto(conceptos,480020,percepcion,"200",LocalDate.of(2027,6,1),LocalDate.of(2027,6,30));concepto(conceptos,480020,deduccion,"200",LocalDate.of(2027,6,1),LocalDate.of(2027,6,30));
   empleado(j,480021);concepto(conceptos,480021,percepcion,"200",LocalDate.of(2027,6,1),LocalDate.of(2027,6,30));concepto(conceptos,480021,deduccion,"300",LocalDate.of(2027,6,1),LocalDate.of(2027,6,30));
   var filtro=new FiltroNomina("","TODOS",null,null,null,null,null,null,null,null,0,25);
   assertThat(nominas.candidatos(junio,filtro,"REVISION")).extracting(PreparacionNominaService.Candidato::motivo).anyMatch(m->m.contains("deducciones superan"));
   var vista=nominas.prepararPeriodo(junio,"TODOS");assertThat(nominas.totalesPreparacion(vista).empleados()).isEqualTo(1);assertThat(nominas.totalesPreparacion(vista).propuesto()).isZero();
   long cero=nominas.guardarPreparacion(vista,nominas.preparacion(vista).version(),"Cierre cero","Prueba aislada");long version=nominas.obtener(cero).version();
   assertThatThrownBy(()->pagos.autorizar(cero,version," ")).isInstanceOf(RuntimeException.class);assertThat(pagos.resumenCero(cero).confirmados()).isZero();
   var pool=java.util.concurrent.Executors.newFixedThreadPool(2);var inicio=new java.util.concurrent.CountDownLatch(1);
   try{var tareas=new ArrayList<java.util.concurrent.Future<Boolean>>();for(int k=0;k<2;k++)tareas.add(pool.submit(()->{SecurityContextHolder.getContext().setAuthentication(auth);try{inicio.await();pagos.autorizar(cero,version,"Confirmación cero ficticia");return true;}catch(escuela.common.exception.ReglaNegocioException ex){return false;}finally{SecurityContextHolder.clearContext();}}));inicio.countDown();int exitos=0;for(var tarea:tareas)if(tarea.get(30,java.util.concurrent.TimeUnit.SECONDS))exitos++;assertThat(exitos).isEqualTo(1);}finally{pool.shutdownNow();}SecurityContextHolder.getContext().setAuthentication(auth);
   assertThat(nominas.obtener(cero).estadoTexto()).isEqualTo("Confirmada sin pagos");
   var fila=pagos.listar(cero,"","CERO_CONFIRMADO",0,25).getContent().getFirst();assertThat(fila.estado()).isEqualTo("CERO_CONFIRMADO");assertThat(fila.pagoId()).isNull();
   assertThat(pagos.listar(cero,"","PENDIENTE",0,25)).isEmpty();assertThat(pagos.cierreCero(cero,fila.id()).motivo()).isEqualTo("Confirmación cero ficticia");
   var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)app).addFilters(app.getBean(org.springframework.security.web.FilterChainProxy.class)).build();var session=new org.springframework.mock.web.MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new org.springframework.security.core.context.SecurityContextImpl(auth));
   var dir=java.nio.file.Files.createDirectories(java.nio.file.Path.of("target/qa-cero"));
   for(var ruta:Map.of("resumen","/admin/preparacion-nomina/"+cero+"/pagos","detalle","/admin/preparacion-nomina/"+cero+"/pagos/"+fila.id()).entrySet()){
    var response=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get(ruta.getValue()).session(session)).andReturn().getResponse();assertThat(response.getStatus()).isEqualTo(200);assertThat(response.getContentAsString()).contains("Neto cero confirmado").doesNotContain("Pagar desde una o varias cuentas");java.nio.file.Files.writeString(dir.resolve(ruta.getKey()+".html"),response.getContentAsString());
   }
   var excel=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/preparacion-nomina/"+cero+"/pagos/excel").session(session).param("estadoPago","CERO_CONFIRMADO")).andReturn().getResponse();assertThat(excel.getStatus()).isEqualTo(200);
   try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getRow(1).getCell(4).getStringCellValue()).isEqualTo("Neto cero confirmado");assertThat(libro.getSheetAt(0).getRow(1).getCell(5).getStringCellValue()).isEmpty();}
   SecurityContextHolder.getContext().setAuthentication(auth);
   assertThat(j.queryForObject("SELECT count(*) FROM nomina_cierre_cero WHERE borrador_id=?",Long.class,cero)).isEqualTo(1);
   assertThatThrownBy(()->pagos.autorizar(cero,version,"Segundo intento")).isInstanceOf(RuntimeException.class);
   assertThatThrownBy(()->j.update("DELETE FROM nomina_cierre_cero WHERE borrador_id=?",cero)).isInstanceOf(RuntimeException.class);
   assertThatThrownBy(()->j.update("UPDATE nomina_cierre_cero SET motivo='Otro' WHERE borrador_id=?",cero)).isInstanceOf(RuntimeException.class);
   assertThat(j.queryForObject("SELECT count(*) FROM nomina_pago_intento",Long.class)).isZero();assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();
   var sinPermiso=List.of(new SimpleGrantedAuthority("EMPLEADO_LEER"));SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new UsuarioPrincipal(480000L,480000L,Set.of(),true,false,"qa.cero","",sinPermiso),"",sinPermiso));
   assertThatThrownBy(()->pagos.cierreCero(cero,fila.id())).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);SecurityContextHolder.getContext().setAuthentication(auth);

   long julio=periodo(periodos,"JUL-VEINTE-QA",LocalDate.of(2027,7,1),LocalDate.of(2027,7,31));
   for(long e=480000;e<480020;e++){empleado(j,e);concepto(conceptos,e,percepcion,"200",LocalDate.of(2027,7,1),null);if(e==480019)concepto(conceptos,e,deduccion,"200",LocalDate.of(2027,7,1),null);}
   var todos=nominas.prepararPeriodo(julio,"TODOS");assertThat(nominas.totalesPreparacion(todos).empleados()).isEqualTo(20);assertThat(nominas.totalesPreparacion(todos).propuesto()).isEqualByComparingTo("3800");
   long lote=nominas.guardarPreparacion(todos,nominas.preparacion(todos).version(),"Veinte empleados","Colectivo con un cero");
   j.update("INSERT INTO cuenta_financiera(id,institucion_id,codigo,nombre,tipo,moneda,saldo_inicial,fecha_saldo_inicial) VALUES(480000,480000,'CAJA-CERO','Caja ficticia','CAJA','MXN',5000,'2026-01-01')");
   j.update("INSERT INTO motivo_financiero(id,institucion_id,codigo,nombre,naturaleza,categoria) VALUES(480000,480000,'NOM-CERO','Prueba ficticia','EGRESO','PERSONAL')");
   var f=new PagoMultipleNominaForm();f.setVersion(nominas.obtener(lote).version());f.setReferencia("VEINTE-QA");f.setMotivo("Prueba veinte empleados, uno cero");f.setMotivoPagoId(480000L);f.getFuentes().getFirst().setCuentaId(480000L);f.getFuentes().getFirst().setImporte(new BigDecimal("3800"));
   var revision=multiple.revisar(lote,f);assertThat(revision.empleados()).isEqualTo(19);assertThat(revision.total()).isEqualByComparingTo("3800");f.setSello(revision.sello());f.setConfirmado(true);
   j.execute("CREATE FUNCTION qa_cero_fallo() RETURNS trigger LANGUAGE plpgsql AS $$ BEGIN IF NEW.referencia='VEINTE-QA' THEN RAISE EXCEPTION 'Fallo ficticio después de cierre cero'; END IF; RETURN NEW; END $$");j.execute("CREATE TRIGGER qa_cero_fallo BEFORE INSERT ON nomina_pago_intento FOR EACH ROW EXECUTE FUNCTION qa_cero_fallo()");
   assertThatThrownBy(()->multiple.pagar(lote,f)).isInstanceOf(RuntimeException.class);assertThat(nominas.obtener(lote).estado()).isEqualTo("BORRADOR");assertThat(pagos.resumenCero(lote).confirmados()).isZero();assertThat(j.queryForObject("SELECT count(*) FROM movimiento_financiero",Long.class)).isZero();j.execute("DROP TRIGGER qa_cero_fallo ON nomina_pago_intento");
   multiple.pagar(lote,f);assertThat(nominas.obtener(lote).estado()).isEqualTo("PAGADA");assertThat(pagos.resumenCero(lote).confirmados()).isEqualTo(1);
   assertThat(pagos.listar(lote,"","TODOS",0,25)).hasSize(20);assertThat(pagos.listar(lote,"","PAGADO",0,25)).hasSize(19);assertThat(pagos.listar(lote,"","PENDIENTE",0,25)).isEmpty();
   assertThat(j.queryForObject("SELECT count(*) FROM nomina_pago_intento",Long.class)).isEqualTo(19);assertThat(j.queryForObject("SELECT sum(monto) FROM movimiento_financiero",BigDecimal.class)).isEqualByComparingTo("3800");
   assertThat(j.queryForObject("SELECT saldo_posterior FROM movimiento_financiero WHERE cuenta_id=480000 ORDER BY secuencia_cuenta DESC LIMIT 1",BigDecimal.class)).isEqualByComparingTo("1200");
   assertThatThrownBy(()->multiple.pagar(lote,f)).isInstanceOf(RuntimeException.class);assertThat(pagos.resumenCero(lote).confirmados()).isEqualTo(1);
  }finally{SecurityContextHolder.clearContext();}
 }
 private static long periodo(ProgramacionNominaService s,String codigo,LocalDate inicio,LocalDate fin){var f=new PeriodoNominaForm();f.setCodigo(codigo);f.setInicio(inicio);f.setFin(fin);f.setPago(fin);return s.guardarPeriodo(null,f);}
 private static void empleado(JdbcTemplate j,long id){j.update("INSERT INTO empleado(id,institucion_id,codigo,nombres,primer_apellido) VALUES(?,480000,?,'Persona','Ficticia')",id,"EMP-CERO-"+id);j.update("INSERT INTO empleado_laboral(empleado_id,situacion,fecha_ingreso) VALUES(?,'ACTIVO','2026-10-01')",id);}
 private static void concepto(ConceptoEmpleadoService s,long empleado,long concepto,String importe,LocalDate inicio,LocalDate fin){var c=s.contextoConfiguracion(empleado,concepto);var f=new ConceptoEmpleadoForm();f.setEmpleadoId(empleado);f.setEmpleadoTexto(c.empleado());f.setConceptoId(concepto);f.setConceptoTexto(c.concepto());f.setConceptoVersion(c.conceptoVersion());f.setLaboralVersion(c.laboralVersion());f.setInicio(inicio);f.setFin(fin);f.setImporte(new BigDecimal(importe));f.setFrecuenciaImporte("MENSUAL");f.setMotivo("Configuración de prueba ficticia");s.asignarConfiguracion(f);}
}
