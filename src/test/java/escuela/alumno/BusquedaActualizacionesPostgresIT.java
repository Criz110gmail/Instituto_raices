package escuela.alumno;

import escuela.SistemaAdministrativoEscolarApplication;
import escuela.alumno.service.BusquedaAlumnosActualizacionesService;
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
import static org.assertj.core.api.Assertions.*;

@EnabledIfEnvironmentVariable(named="QA_ACTUALIZACIONES_JDBC",matches="jdbc:postgresql://127.0.0.1:5432/nexo_actualizaciones_qa")
class BusquedaActualizacionesPostgresIT {
 @Test void buscaSoloAlumnosConSolicitudesDeInstitucionAutorizada() {
  try(var app=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run("--server.port=0","--spring.config.import=","--spring.datasource.url="+System.getenv("QA_ACTUALIZACIONES_JDBC"),"--spring.datasource.username=postgres","--spring.datasource.password=qa-ficticia","--spring.security.user.name=qa","--spring.security.user.password=qa-ficticia")) {
   var j=app.getBean(JdbcTemplate.class);
   j.update("INSERT INTO institucion(id,codigo,nombre,zona_horaria,moneda_predeterminada) VALUES(690000,'QA-A','Prueba A','America/Mexico_City','MXN'),(690001,'QA-B','Prueba B','America/Mexico_City','MXN')");
   j.update("INSERT INTO usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) VALUES(690000,690000,'qa.actualizaciones','qa-actualizaciones@example.invalid','ADMINISTRATIVO','ACTIVO','ficticia')");
   j.update("INSERT INTO alumno(id,institucion_id,matricula,nombres,primer_apellido,fecha_nacimiento,fecha_ingreso) VALUES(690000,690000,'A-PRUEBA','Ana','Largo','2015-01-01','2026-01-01'),(690001,690000,'A-SIN','Sin','Solicitud','2015-01-01','2026-01-01'),(690002,690001,'B-PRUEBA','Berta','Ajena','2015-01-01','2026-01-01')");
   j.update("INSERT INTO tutor(id,institucion_id,nombres,primer_apellido,telefono_principal) VALUES(690000,690000,'Tutor','Prueba','000'),(690001,690001,'Tutor','Ajeno','000')");
   j.update("INSERT INTO actualizacion_expediente_familiar(id,institucion_id,alumno_id,tutor_id,tipo,estado,consentimiento_version,consentimiento_texto,consentimiento_en,tipo_sanguineo) VALUES(690000,690000,690000,690000,'FICHA_MEDICA','ENVIADA','QA','Prueba ficticia',now(),'DESCONOCIDO'),(690001,690001,690002,690001,'FICHA_MEDICA','ENVIADA','QA','Prueba ficticia',now(),'DESCONOCIDO')");
   var autoridades=List.of(new SimpleGrantedAuthority("ACTUALIZACION_EXPEDIENTE_LEER"),new SimpleGrantedAuthority("ACTUALIZACION_EXPEDIENTE_REVISAR"));
   var p=new UsuarioPrincipal(690000L,690000L,Set.of(),true,false,"qa","",autoridades);
   SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(p,"",autoridades));
   var s=app.getBean(BusquedaAlumnosActualizacionesService.class);
   assertThat(s.buscar(690000L,"").resultados()).extracting(x->x.id()).containsExactly(690000L);
   assertThat(s.buscar(690000L,"Ana").resultados()).extracting(x->x.id()).containsExactly(690000L);
   assertThat(s.buscar(690000L,"An").resultados()).isEmpty();
   assertThat(s.buscar(690000L,"Solicitud").resultados()).isEmpty();
   assertThatThrownBy(()->s.buscar(690001L,"")).isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
   try {
    var mvc=org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup((org.springframework.web.context.WebApplicationContext)app).addFilters(app.getBean(org.springframework.security.web.FilterChainProxy.class)).build();
    var session=new org.springframework.mock.web.MockHttpSession();session.setAttribute("SPRING_SECURITY_CONTEXT",new org.springframework.security.core.context.SecurityContextImpl(new UsernamePasswordAuthenticationToken(p,"",autoridades)));
    var busqueda=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/actualizaciones-expediente/alumnos/buscar").session(session).param("institucionId","690000").param("q","Ana")).andReturn().getResponse();
    assertThat(busqueda.getStatus()).isEqualTo(200);assertThat(busqueda.getContentAsString()).contains("A-PRUEBA").doesNotContain("B-PRUEBA");
    var denegado=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/actualizaciones-expediente/alumnos/buscar").session(session).param("institucionId","690001")).andReturn().getResponse();assertThat(denegado.getStatus()).isEqualTo(403);
    var lista=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/actualizaciones-expediente").session(session).param("institucionId","690000").param("alumnoId","690000").param("alumnoTexto","A-PRUEBA · Ana Largo")).andReturn().getResponse();
    assertThat(lista.getStatus()).isEqualTo(200);assertThat(lista.getContentAsString()).contains("A-PRUEBA","alumnoId=690000","Revisar");
    var retorno="/admin/actualizaciones-expediente?institucionId=690000&alumnoId=690000&pagina=0&tamanio=25";
    var detalle=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/actualizaciones-expediente/690000").session(session).param("retorno",retorno)).andReturn().getResponse();
    assertThat(detalle.getStatus()).isEqualTo(200);assertThat(detalle.getContentAsString()).contains("update-resumen","update-propuesta","update-decision","Volver a la bandeja");
    var dir=java.nio.file.Files.createDirectories(java.nio.file.Path.of("target/qa-actualizaciones"));java.nio.file.Files.writeString(dir.resolve("listado.html"),lista.getContentAsString());java.nio.file.Files.writeString(dir.resolve("detalle.html"),detalle.getContentAsString());
    var excel=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/admin/actualizaciones-expediente/excel").session(session).param("institucionId","690000").param("alumnoId","690000")).andReturn().getResponse();assertThat(excel.getStatus()).isEqualTo(200);try(var libro=new org.apache.poi.xssf.usermodel.XSSFWorkbook(new java.io.ByteArrayInputStream(excel.getContentAsByteArray()))){assertThat(libro.getSheetAt(0).getLastRowNum()).isEqualTo(1);}
    assertThat(detalle.getContentAsString()).contains("action=\"/admin/actualizaciones-expediente/690000/rechazar\"", "data-review-decision=\"aprobar\"", "update-confirm-dialog", "name=\"_csrf\"");
    var rechazo=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/actualizaciones-expediente/690000/rechazar").session(session).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("version","0").param("respuesta","Motivo de prueba")).andReturn().getResponse();
    assertThat(rechazo.getStatus()).isEqualTo(302);
    assertThat(j.queryForObject("SELECT estado FROM actualizacion_expediente_familiar WHERE id=690000",String.class)).isEqualTo("RECHAZADA");
    assertThat(j.queryForObject("SELECT count(*) FROM ficha_medica_alumno WHERE alumno_id=690000",Integer.class)).isZero();
    j.update("INSERT INTO actualizacion_expediente_familiar(id,institucion_id,alumno_id,tutor_id,tipo,estado,consentimiento_version,consentimiento_texto,consentimiento_en,tipo_sanguineo) VALUES(690002,690000,690000,690000,'FICHA_MEDICA','ENVIADA','QA','Prueba ficticia',now(),'DESCONOCIDO')");
    var aprobacion=mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/admin/actualizaciones-expediente/690002/aprobar").session(session).with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()).param("version","0").param("respuesta","Aprobación de prueba")).andReturn().getResponse();
    assertThat(aprobacion.getStatus()).isEqualTo(302);
    assertThat(j.queryForObject("SELECT estado FROM actualizacion_expediente_familiar WHERE id=690002",String.class)).isEqualTo("APROBADA");
    assertThat(j.queryForObject("SELECT count(*) FROM ficha_medica_alumno WHERE alumno_id=690000",Integer.class)).isEqualTo(1);
   } catch(Exception ex){throw new AssertionError(ex);}

  } finally {SecurityContextHolder.clearContext();}
 }
}
