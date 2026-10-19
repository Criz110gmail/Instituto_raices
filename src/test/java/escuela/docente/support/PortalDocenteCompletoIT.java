package escuela.docente.support;

import escuela.SistemaAdministrativoEscolarApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.assertj.core.api.Assertions.*;

/** Aplicación y filtros reales, sólo PostgreSQL desechable y cuenta ficticia. */
@EnabledIfEnvironmentVariable(named="QA_PORTAL_JDBC_URL",matches=".+")
class PortalDocenteCompletoIT {
 @Test void loginYPrimeraPantallaConPrincipalDocenteReal()throws Exception{
  java.nio.file.Files.createDirectories(java.nio.file.Path.of("target"));
  var storage=java.nio.file.Files.createTempDirectory(java.nio.file.Path.of("target"),"qa-portal-docente-").toAbsolutePath();
  try(var c=new SpringApplicationBuilder(SistemaAdministrativoEscolarApplication.class).run(
   "--server.port=0","--spring.datasource.url="+System.getenv("QA_PORTAL_JDBC_URL"),"--spring.datasource.username=postgres","--spring.datasource.password=qa_portal_aislado",
   "--spring.security.user.name=recuperacion-qa","--spring.security.user.password=recuperacion-ficticia-sin-uso","--app.archivos.directorio="+storage)){
   var jdbc=c.getBean(JdbcTemplate.class);
   jdbc.update("insert into institucion(id,codigo,nombre) values(9100,'QA-DOC','Institución ficticia de QA')");
   jdbc.update("insert into usuario(id,institucion_id,username,email,tipo_cuenta,estado,password_hash) values(9100,9100,'docente.ficticio','docente@example.invalid','PORTAL_MAESTRO','ACTIVO',?)",c.getBean(PasswordEncoder.class).encode("clave-ficticia-docente"));
   jdbc.update("insert into empleado(id,institucion_id,codigo,nombres,primer_apellido) values(9100,9100,'EMP-QA','Docente','Ficticio')");
   jdbc.update("insert into maestro(id,institucion_id,empleado_id,numero_empleado,usuario_id) values(9100,9100,9100,'DOC-QA',9100)");
   var mvc=MockMvcBuilders.webAppContextSetup((WebApplicationContext)c).addFilters(c.getBean(FilterChainProxy.class)).build();
   var tokenResult=mvc.perform(get("/acceso/csrf")).andReturn();
   var sesion=(MockHttpSession)tokenResult.getRequest().getSession(false);
   var token=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(tokenResult.getResponse().getContentAsString());assertThat(token.find()).isTrue();
   var login=mvc.perform(post("/login").session(sesion).param("_csrf",token.group(1)).param("origen","maestros").param("username","docente.ficticio").param("password","clave-ficticia-docente")).andReturn();
   assertThat(login.getResponse().getRedirectedUrl()).isEqualTo("/maestros");
   var pagina=mvc.perform(get("/maestros").session(sesion)).andReturn();
   assertThat(pagina.getResponse().getStatus()).as("GET docente luego de login: %s",pagina.getResolvedException()).isEqualTo(200);
   assertThat(pagina.getResponse().getContentAsString()).contains("Mis planeaciones","Docente Ficticio").doesNotContain("No tienes acceso a esta información");
   assertThat(mvc.perform(get("/admin").session(sesion)).andReturn().getResponse().getRedirectedUrl()).isEqualTo("/maestros");
   var denegado=mvc.perform(get("/acceso-denegado").session(sesion)).andReturn().getResponse();
   assertThat(denegado.getContentAsString()).contains("Ir al portal de maestros","href=\"/maestros\"").doesNotContain("href=\"/admin\"");
   assertThat(mvc.perform(get("/admin/maestros/9100/editar").session(sesion)).andReturn().getResponse().getStatus()).isEqualTo(403);
   // A diferencia de MockMvc, Tomcat ejecuta los redispatch/forward y cookies reales.
   var servidor=(org.springframework.boot.web.server.context.WebServerApplicationContext)c;
   String base="http://127.0.0.1:"+servidor.getWebServer().getPort();
   var client=java.net.http.HttpClient.newBuilder().cookieHandler(new java.net.CookieManager(null,java.net.CookiePolicy.ACCEPT_ALL)).followRedirects(java.net.http.HttpClient.Redirect.NORMAL).build();
   var csrfHttp=client.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(base+"/acceso/csrf")).GET().build(),java.net.http.HttpResponse.BodyHandlers.ofString());
   var csrfToken=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(csrfHttp.body());assertThat(csrfToken.find()).isTrue();
   String csrfForm="_csrf="+java.net.URLEncoder.encode(csrfToken.group(1),java.nio.charset.StandardCharsets.UTF_8);
   var comprobar=client.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(base+"/acceso/comprobar")).header("Content-Type","application/x-www-form-urlencoded").POST(java.net.http.HttpRequest.BodyPublishers.ofString(csrfForm)).build(),java.net.http.HttpResponse.BodyHandlers.ofString());
   assertThat(comprobar.statusCode()).isEqualTo(204);
   var real=client.send(java.net.http.HttpRequest.newBuilder(java.net.URI.create(base+"/login")).header("Content-Type","application/x-www-form-urlencoded").POST(java.net.http.HttpRequest.BodyPublishers.ofString(csrfForm+"&origen=maestros&username=docente.ficticio&password=clave-ficticia-docente")).build(),java.net.http.HttpResponse.BodyHandlers.ofString());
   assertThat(real.uri().getPath()).as("Destino real tras redirects").isEqualTo("/maestros");
   assertThat(real.statusCode()).isEqualTo(200);
   assertThat(real.body()).contains("Mis planeaciones","Docente Ficticio").doesNotContain("No tienes acceso a esta información");
  }
 }
}
