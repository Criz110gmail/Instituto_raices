package escuela.admin.service;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.seguridad.service.UsuarioPrincipal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.assertj.core.api.Assertions.assertThat;

@EnabledIfEnvironmentVariable(named="QA_CONCEPTOS_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_conceptos_qa")
class ConceptoFiltroPostgresIT {
 @Test void limitaConceptosHistoricosAlPlantelAutorizado() {
  try(var app = new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_CONCEPTOS_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-ficticia","--spring.security.user.name=qa","--spring.security.user.password=qa-ficticia")) {
   var j=app.getBean(JdbcTemplate.class);
   j.update("INSERT INTO institucion(id,codigo,nombre) VALUES(690000,'QA','Institucion ficticia')");
   j.update("INSERT INTO plantel(id,institucion_id,codigo,nombre) VALUES(690000,690000,'A','Plantel A'),(690001,690000,'B','Plantel B')");
   j.update("INSERT INTO nivel_educativo(id,institucion_id,codigo,nombre,orden) VALUES(690000,690000,'N','Nivel',1)");
   j.update("INSERT INTO grado(id,nivel_educativo_id,codigo,nombre,orden) VALUES(690000,690000,'G','Grado',1)");
   j.update("INSERT INTO ciclo_escolar(id,institucion_id,codigo,nombre,fecha_inicio,fecha_fin,estado) VALUES(690000,690000,'C','Ciclo','2026-01-01','2026-12-31','ABIERTO')");
   j.update("INSERT INTO alumno(id,institucion_id,matricula,nombres,primer_apellido,fecha_nacimiento,fecha_ingreso) VALUES(690000,690000,'A','Ana','Prueba','2015-01-01','2026-01-01'),(690001,690000,'B','Beto','Prueba','2015-01-01','2026-01-01')");
   j.update("INSERT INTO inscripcion(id,alumno_id,plantel_id,ciclo_escolar_id,grado_id,numero_inscripcion,fecha_inscripcion,fecha_inicio,estado) VALUES(690000,690000,690000,690000,690000,'A','2026-01-01','2026-01-01','ACTIVA'),(690001,690001,690001,690000,690000,'B','2026-01-01','2026-01-01','ACTIVA')");
   j.update("INSERT INTO concepto_cobro(id,institucion_id,codigo,nombre,categoria,activo) VALUES(690000,690000,'COLE-A','Colegiatura accesible','COLEGIATURA',false),(690001,690000,'COLE-B','Colegiatura ajena','COLEGIATURA',true)");
   j.update("INSERT INTO cuota_alumno(inscripcion_id,concepto_cobro_id,importe_base,moneda,frecuencia,fecha_inicio,fecha_fin,fecha_vencimiento_unico,estado) VALUES(690000,690000,100,'MXN','UNICA','2026-01-01','2026-12-31','2026-02-01','ACTIVA'),(690001,690001,100,'MXN','UNICA','2026-01-01','2026-12-31','2026-02-01','ACTIVA')");
   var auth=List.of(new SimpleGrantedAuthority("CUOTA_ALUMNO_LEER"));
   var principal=new UsuarioPrincipal(1L,690000L,Set.of(690000L),false,false,"qa","",auth);
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(principal,"",auth));
   var service=app.getBean(ConceptoFiltroBusquedaService.class);
   var alumnos=app.getBean(AlumnoCobranzaBusquedaService.class);
   assertThat(alumnos.buscar("",false).resultados()).extracting(x->x.id()).containsExactly(690000L);
   assertThat(alumnos.buscar("Beto",false).resultados()).isEmpty();
   assertThat(service.buscar("").resultados()).extracting(x->x.titulo()).containsExactly("COLE-A");
   assertThat(service.buscar("Colegiatura").resultados()).extracting(x->x.titulo()).containsExactly("COLE-A");
   assertThat(service.buscar("co").resultados()).isEmpty();
   assertThat(service.buscar("ajena").resultados()).isEmpty();
   try {
    var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)app).addFilters(app.getBean(org.springframework.security.web.FilterChainProxy.class)).build();
    var session=new org.springframework.mock.web.MockHttpSession();
    session.setAttribute("SPRING_SECURITY_CONTEXT",new org.springframework.security.core.context.SecurityContextImpl(new UsernamePasswordAuthenticationToken(principal,"",auth)));
    var busqueda=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/autocompletado/conceptos-filtro").session(session).param("q","cole")).andReturn().getResponse();
    assertThat(busqueda.getStatus()).isEqualTo(200);assertThat(busqueda.getContentAsString()).contains("COLE-A").doesNotContain("COLE-B");
    var lista=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/cuotas-alumno").session(session).param("conceptoCobro","COLE-A")).andReturn().getResponse();
    assertThat(lista.getStatus()).isEqualTo(200);assertThat(lista.getContentAsString()).contains("Colegiatura accesible", "conceptoCobro=COLE-A").doesNotContain("Colegiatura ajena");
    var alumnoFiltrado=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/cuotas-alumno").session(session).param("alumnoId","690000").param("alumnoTexto","A · Ana Prueba")).andReturn().getResponse();
    assertThat(alumnoFiltrado.getStatus()).isEqualTo(200);assertThat(alumnoFiltrado.getContentAsString()).contains("Ana Prueba","alumnoId=690000").doesNotContain("Beto Prueba");
    var alumnoAjeno=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/cuotas-alumno").session(session).param("alumnoId","690001").param("alumnoTexto","B · Beto Prueba")).andReturn().getResponse();
    assertThat(alumnoAjeno.getContentAsString()).doesNotContain("Colegiatura ajena");
    var excelAlumno=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/cuotas-alumno/excel").session(session).param("alumnoId","690000").param("alumnoTexto","A · Ana Prueba")).andReturn().getResponse();
    try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excelAlumno.getContentAsByteArray()))) {assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);}
    var excel=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/cuotas-alumno/excel").session(session).param("conceptoCobro","COLE-A")).andReturn().getResponse();
    assertThat(excel.getStatus()).isEqualTo(200);
    try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excel.getContentAsByteArray()))) {assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);}
   } catch(Exception ex){throw new AssertionError(ex);}
   var soloPagos=List.of(new SimpleGrantedAuthority("PAGO_LEER"));
   var pagoPrincipal=new UsuarioPrincipal(1L,690000L,Set.of(690000L),false,false,"qa","",soloPagos);
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(pagoPrincipal,"",soloPagos));
   assertThat(service.buscar("").resultados()).isEmpty();
   j.update("INSERT INTO tutor(id,institucion_id,nombres,primer_apellido,telefono_principal) VALUES(690000,690000,'Tutor','Prueba','000')");
   j.update("INSERT INTO cargo(id,inscripcion_id,concepto_cobro_id,clave_generacion,descripcion,periodo_cobro_inicio,periodo_cobro_fin,fecha_emision,fecha_vencimiento,importe_original,moneda) VALUES(690000,690000,690000,'QA-CARGO','Cargo ficticio','2026-01-01','2026-01-31','2026-01-01','2026-01-31',100,'MXN')");
   j.update("INSERT INTO pago(id,institucion_id,plantel_registro_id,tutor_id,folio,clave_idempotencia,fecha_pago,monto,moneda,metodo) VALUES(690000,690000,690000,690000,'QA-PAGO','QA-CLAVE',now(),100,'MXN','EFECTIVO')");
   j.update("INSERT INTO solicitud_aplicacion_pago(pago_id,cargo_id,monto_solicitado) VALUES(690000,690000,100)");
   assertThat(service.buscar("cole").resultados()).extracting(x->x.titulo()).containsExactly("COLE-A");
   verificarListado(app,pagoPrincipal,soloPagos,"pagos","QA-PAGO");
   var soloCargos=List.of(new SimpleGrantedAuthority("CARGO_LEER"));
   var cargoPrincipal=new UsuarioPrincipal(1L,690000L,Set.of(690000L),false,false,"qa","",soloCargos);
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(cargoPrincipal,"",soloCargos));
   assertThat(service.buscar("cole").resultados()).extracting(x->x.titulo()).containsExactly("COLE-A");

   assertThat(alumnos.buscar("Ana",true).resultados()).extracting(x->x.id()).containsExactly(690000L);
   assertThat(alumnos.buscar("Beto",true).resultados()).isEmpty();
   verificarListado(app,cargoPrincipal,soloCargos,"cargos","Cargo ficticio");
  } finally {SecurityContextHolder.clearContext();}
 }
 private void verificarListado(org.springframework.context.ConfigurableApplicationContext app, UsuarioPrincipal principal, List<SimpleGrantedAuthority> auth, String modulo, String contenido) {
  try {
   var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)app).addFilters(app.getBean(org.springframework.security.web.FilterChainProxy.class)).build();
   var sesion=new org.springframework.mock.web.MockHttpSession();
   sesion.setAttribute("SPRING_SECURITY_CONTEXT",new org.springframework.security.core.context.SecurityContextImpl(new UsernamePasswordAuthenticationToken(principal,"",auth)));
   var lista=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/"+modulo).session(sesion).param("conceptoCobro","COLE-A")).andReturn().getResponse();
   assertThat(lista.getStatus()).isEqualTo(200);assertThat(lista.getContentAsString()).contains(contenido,"conceptoCobro=COLE-A");
   var porAlumno=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/"+modulo).session(sesion).param("alumnoId","690000").param("alumnoTexto","A · Ana Prueba")).andReturn().getResponse();
   assertThat(porAlumno.getStatus()).isEqualTo(200);assertThat(porAlumno.getContentAsString()).contains(contenido,"alumnoId=690000");
   var excelAlumno=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/"+modulo+"/excel").session(sesion).param("alumnoId","690000").param("alumnoTexto","A · Ana Prueba")).andReturn().getResponse();
   assertThat(excelAlumno.getStatus()).isEqualTo(200);
   try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excelAlumno.getContentAsByteArray()))) {assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);}
   var excel=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/catalogos/"+modulo+"/excel").session(sesion).param("conceptoCobro","COLE-A")).andReturn().getResponse();
   assertThat(excel.getStatus()).isEqualTo(200);
   try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excel.getContentAsByteArray()))) {assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);}
  } catch(Exception ex){throw new AssertionError(ex);}
 }

}
