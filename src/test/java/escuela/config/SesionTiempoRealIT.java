package escuela.config;

import escuela.inicio.controller.LoginController;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.regex.Pattern;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import static org.assertj.core.api.Assertions.assertThat;

/** Ejecución optativa de más de cinco minutos: Tomcat, cookies y usuario ficticio, sin BD. */
@EnabledIfSystemProperty(named="qa.sesion.tiempoReal",matches="true")
class SesionTiempoRealIT {
 @Test void inactividadRealCincoMinutosYActividadRenueva() throws Exception {
  var tomcat=new Tomcat(); Path base=Path.of("target/qa-sesion-tiempo-real"); Files.createDirectories(base);
  tomcat.setBaseDir(base.toAbsolutePath().toString());tomcat.setPort(0);
  var connector=tomcat.getConnector();connector.setProperty("address","127.0.0.1");
  var ctx=tomcat.addContext("",base.toAbsolutePath().toString());ctx.setSessionTimeout(5);
  try(var spring=new AnnotationConfigWebApplicationContext()) {
   spring.setServletContext(ctx.getServletContext());spring.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,
    SesionExpiradaAccessDeniedHandler.class,SesionController.class,LoginController.class,
    LoginRenderSinSesionTest.Vistas.class,LoginCookieHttpTest.Recursos.class,LoginCookieHttpTest.Paginas.class);spring.refresh();
   Tomcat.addServlet(ctx,"mvc",new DispatcherServlet(spring)).setLoadOnStartup(1);ctx.addServletMappingDecoded("/","mvc");
   var filtro=new FilterDef();filtro.setFilterName("security");filtro.setFilter(new DelegatingFilterProxy("springSecurityFilterChain",spring));ctx.addFilterDef(filtro);
   var mapa=new FilterMap();mapa.setFilterName("security");mapa.addURLPattern("/*");ctx.addFilterMap(mapa);tomcat.start();
   var http=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL)).followRedirects(HttpClient.Redirect.NEVER).connectTimeout(Duration.ofSeconds(5)).build();
   String host="http://127.0.0.1:"+connector.getLocalPort();
   var csrf=get(http,host,"/acceso/csrf");assertThat(csrf.statusCode()).isEqualTo(200);
   String token=stringField(csrf.body(),"token");
   var entrada=post(http,host,"/login","_csrf="+URLEncoder.encode(token,StandardCharsets.UTF_8)+"&origen=admin&username=usuario-qa&password=clave-qa");
   assertThat(entrada.statusCode()).isEqualTo(302);assertThat(get(http,host,"/admin").statusCode()).isEqualTo(200);
   var primera=get(http,host,"/sesion/estado");assertThat(primera.statusCode()).isEqualTo(200);
   assertThat(numberField(primera.body(),"duracionSegundos")).isEqualTo(300);
   long vencimientoAntes=numberField(primera.body(),"vence");
   System.out.println("QA_SESION: inicio confirmado, duración 300 segundos; se probará renovación por navegación.");
   Thread.sleep(10000);assertThat(get(http,host,"/admin").statusCode()).isEqualTo(200);
   var renovada=get(http,host,"/sesion/estado");assertThat(renovada.statusCode()).isEqualTo(200);
   long vencimiento=numberField(renovada.body(),"vence"),ahora=numberField(renovada.body(),"ahora");
   assertThat(vencimiento-vencimientoAntes).isGreaterThanOrEqualTo(8000);
   long meta=System.nanoTime()+Duration.ofMillis(vencimiento-ahora).toNanos();
   System.out.println("QA_SESION: navegación renovó el plazo; comienza espera real de inactividad.");
   while(true){long restante=(meta-System.nanoTime())/1_000_000; if(restante<=6000)break;
    Thread.sleep(Math.min(30000,restante-5000));var r=get(http,host,"/sesion/estado");
    assertThat(r.statusCode()).as("La consulta no debe cerrar antes del vencimiento").isEqualTo(200);
    assertThat(numberField(r.body(),"vence")).as("El sondeo no debe renovar").isEqualTo(vencimiento);
    System.out.println("QA_SESION: activa; faltan aproximadamente "+Math.max(0,(meta-System.nanoTime())/1_000_000_000)+" segundos.");
   }
   long espera=(meta-System.nanoTime())/1_000_000+2500;if(espera>0)Thread.sleep(espera);
   var caducada=get(http,host,"/sesion/estado");assertThat(caducada.statusCode()).isEqualTo(401);
   System.out.println("QA_SESION: vencida después de los 300 segundos; resultado HTTP 401 correcto.");
  } finally {tomcat.stop();tomcat.destroy();}
 }
 private static HttpResponse<String> get(HttpClient h,String host,String ruta) throws Exception {return h.send(HttpRequest.newBuilder(URI.create(host+ruta)).timeout(Duration.ofSeconds(5)).GET().build(),HttpResponse.BodyHandlers.ofString());}
 private static HttpResponse<String> post(HttpClient h,String host,String ruta,String body) throws Exception {return h.send(HttpRequest.newBuilder(URI.create(host+ruta)).timeout(Duration.ofSeconds(5)).header("Content-Type","application/x-www-form-urlencoded").POST(HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());}
 private static String stringField(String json,String field){var m=Pattern.compile("\\\""+field+"\\\":\\\"([^\\\"]+)\\\"").matcher(json);assertThat(m.find()).isTrue();return m.group(1);}
 private static long numberField(String json,String field){var m=Pattern.compile("\\\""+field+"\\\":([0-9]+)").matcher(json);assertThat(m.find()).isTrue();return Long.parseLong(m.group(1));}
}
