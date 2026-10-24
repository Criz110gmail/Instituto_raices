package escuela.personal;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/** Se ejecuta exclusivamente dentro del IT con PostgreSQL ficticio y aislado. */
final class ConfiguracionIntegradaVerificacion {
 static void verificar(ApplicationContext app,JdbcTemplate j,MockMvc mvc,MockHttpSession session,UsernamePasswordAuthenticationToken auth,Path dir)throws Exception {
  SecurityContextHolder.getContext().setAuthentication(auth);
  j.update("insert into empleado(id,institucion_id,codigo,nombres,primer_apellido) values(310050,310000,'EMP-UNIFICADO-QA','Empleado','Ficticio')");
  j.update("insert into empleado_laboral(empleado_id,puesto,fecha_ingreso,situacion) values(310050,'Auxiliar',date '2026-01-01','ACTIVO')");
  j.update("insert into concepto_nomina(id,institucion_id,codigo,nombre,naturaleza,aplicacion,creado_por_id,actualizado_por_id) values(310050,310000,'DED-UNIFICADA','Deducción de prueba','DEDUCCION','HABITUAL',310000,310000)");
  var s=app.getBean(ConfiguracionEmpleadoNominaService.class);var consulta=app.getBean(ConfiguracionNominaService.class);var ce=app.getBean(ConceptoEmpleadoService.class);
  var f=s.preparar(310050L,null);f.setCambiarSueldo(true);f.setCambiarCalendario(true);f.setMotivo("Configuración integrada ficticia");
  f.getSueldo().setPuestoId(310000L);f.getSueldo().setPuestoTexto("DOC-CONFIG · Docente de prueba");f.getSueldo().setImporteBruto(new BigDecimal("12000"));f.getSueldo().setFechaInicio(LocalDate.of(2030,1,1));f.getSueldo().setFechaFin(LocalDate.of(2030,12,31));
  f.getCalendario().setCalendarioId(310000L);f.getCalendario().setCalendarioTexto(consulta.calendario(310000L,true));
  f.getConceptos().add(concepto(ce,310000L,"500",LocalDate.of(2030,1,1)));f.getConceptos().add(concepto(ce,310050L,"250",LocalDate.of(2030,6,1)));
  s.guardar(310050L,f);
  assertThat(consulta.obtener(310050L,LocalDate.of(2030,5,15)).deducciones()).isZero();assertThat(consulta.obtener(310050L,LocalDate.of(2030,6,15)).deducciones()).isEqualByComparingTo("250");
  assertThat(consulta.obtener(310050L,LocalDate.of(2030,6,15)).sueldo()).isEqualByComparingTo("12000");
  assertThat(j.queryForObject("select count(*) from condicion_salarial where empleado_id=310050",Long.class)).isEqualTo(1);
  assertThat(j.queryForObject("select count(*) from esquema_remuneracion",Long.class)).isZero();
  assertThatThrownBy(()->s.guardar(310050L,f)).hasMessageContaining("otra pestaña");
  var rollback=s.preparar(310050L,null);rollback.setCambiarSueldo(true);rollback.setCambiarCalendario(true);rollback.setMotivo("Debe revertirse todo por solapamiento");
  rollback.getSueldo().setPuestoId(310000L);rollback.getSueldo().setPuestoTexto("DOC-CONFIG · Docente de prueba");rollback.getSueldo().setImporteBruto(new BigDecimal("13000"));rollback.getSueldo().setFechaInicio(LocalDate.of(2031,1,1));
  rollback.getCalendario().setCalendarioId(310001L);rollback.getCalendario().setCalendarioTexto(consulta.calendario(310001L,true));rollback.getConceptos().add(concepto(ce,310000L,"600",LocalDate.of(2030,2,1)));
  assertThatThrownBy(()->s.guardar(310050L,rollback)).hasMessageContaining("Ya existe este concepto");
  assertThat(j.queryForObject("select count(*) from condicion_salarial where empleado_id=310050",Long.class)).isEqualTo(1);assertThat(consulta.obtener(310050L,LocalDate.of(2030,6,15)).calendarioId()).isEqualTo(310000L);
  assertThat(j.queryForObject("select count(*) from empleado_calendario_nomina_historial where empleado_id=310050",Long.class)).isEqualTo(1);
  var anterior=j.queryForObject("select id from concepto_empleado where empleado_id=310050 and concepto_id=310000",Long.class);
  var cambio=s.preparar(310050L,anterior);cambio.setMotivo("Cambio de bono efectivo en junio");cambio.getConceptos().getFirst().setInicio(LocalDate.of(2030,6,1));cambio.getConceptos().getFirst().setImporte(new BigDecimal("700"));s.guardar(310050L,cambio);
  assertThat(consulta.obtener(310050L,LocalDate.of(2030,5,31)).percepciones()).isEqualByComparingTo("500");assertThat(consulta.obtener(310050L,LocalDate.of(2030,6,1)).percepciones()).isEqualByComparingTo("700");
  assertThat(j.queryForObject("select count(*) from condicion_salarial where empleado_id=310050",Long.class)).isEqualTo(1);
  assertThatThrownBy(()->s.preparar(310010L,anterior)).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  var nuevo=mvc.perform(get("/admin/configuracion-nomina/310050/editar").session(session).param("fecha","2030-06-15").param("q","Origen").param("pagina","2")).andReturn();
  assertThat(nuevo.getResponse().getStatus()).isEqualTo(200);var html=nuevo.getResponse().getContentAsString();assertThat(html).contains("data-money-confirm=\"configuracion-integrada\"","config-concept-template","sueldo.revision","Configurar","pagina=2");Files.writeString(dir.resolve("integrada.html"),html);
  var token=(CsrfToken)nuevo.getRequest().getAttribute(CsrfToken.class.getName());
  var mal=mvc.perform(post("/admin/configuracion-nomina/310050/editar").session(session).param(token.getParameterName(),token.getToken()).param("motivo","Captura conservada de prueba").param("cambiarSueldo","true").param("sueldo.importeBruto","-5")).andReturn().getResponse();assertThat(mal.getStatus()).isEqualTo(200);assertThat(mal.getContentAsString()).contains("Captura conservada de prueba","No se pudo completar la operación");
  assertThat(mvc.perform(post("/admin/configuracion-nomina/310050/editar").session(session)).andReturn().getResponse().getStatus()).isBetween(300,499);
  var vista=mvc.perform(get("/admin/configuracion-nomina/310050").session(session).param("fecha","2030-05-15")).andReturn().getResponse();assertThat(vista.getStatus()).isEqualTo(200);assertThat(vista.getContentAsString()).contains("01/06/2030","$700.00","Programar cambio");Files.writeString(dir.resolve("integrada-detalle.html"),vista.getContentAsString());
  // POST real de la ruta integrada, sin sueldo marcado: no duplica la percepción base.
  SecurityContextHolder.getContext().setAuthentication(auth);var web=s.preparar(310050L,null);
  var ok=mvc.perform(post("/admin/configuracion-nomina/310050/editar").session(session).param(token.getParameterName(),token.getToken()).param("motivo","Cambio organizativo por captura integrada").param("versionLaboral",web.getVersionLaboral().toString()).param("revisionConceptos",web.getRevisionConceptos().toString()).param("sueldo.revision",web.getSueldo().getRevision().toString()).param("calendario.version",web.getCalendario().getVersion().toString()).param("cambiarCalendario","true").param("calendario.calendarioId","310001").param("calendario.calendarioTexto",consulta.calendario(310001L,true)).param("q","Origen").param("pagina","2").param("fecha","2030-05-15")).andReturn().getResponse();
  assertThat(ok.getStatus()).isEqualTo(302);assertThat(ok.getRedirectedUrl()).contains("q=Origen","pagina=2","fecha=2030-05-15");assertThat(j.queryForObject("select count(*) from condicion_salarial where empleado_id=310050",Long.class)).isEqualTo(1);
  assertThat(j.queryForObject("select count(*) from movimiento_financiero",Long.class)).isZero();assertThat(j.queryForObject("select count(*) from nomina_borrador",Long.class)).isZero();
  var permisos=java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("EMPLEADO_LEER"),new org.springframework.security.core.authority.SimpleGrantedAuthority("EMPLEADO_LABORAL_LEER"),new org.springframework.security.core.authority.SimpleGrantedAuthority("CONDICION_SALARIAL_LEER"));
  var lectura=new MockHttpSession();lectura.setAttribute("SPRING_SECURITY_CONTEXT",new org.springframework.security.core.context.SecurityContextImpl(new UsernamePasswordAuthenticationToken(new escuela.seguridad.service.UsuarioPrincipal(310000L,310000L,java.util.Set.of(),true,false,"qa.lectura","",permisos),"",permisos)));
  assertThat(mvc.perform(get("/admin/configuracion-nomina/310050/editar").session(lectura)).andReturn().getResponse().getStatus()).isBetween(300,499);
  assertThat(mvc.perform(get("/admin/configuracion-nomina/310050").session(lectura)).andReturn().getResponse().getContentAsString()).doesNotContain(">Configurar nómina</a>",">Programar cambio</a>");
 }
 private static ConceptoEmpleadoForm concepto(ConceptoEmpleadoService ce,Long id,String monto,LocalDate inicio){var f=new ConceptoEmpleadoForm();var c=ce.contexto(310050L,id);f.setConceptoId(id);f.setConceptoTexto(c.concepto());f.setConceptoVersion(c.conceptoVersion());f.setImporte(new BigDecimal(monto));f.setInicio(inicio);return f;}
}
