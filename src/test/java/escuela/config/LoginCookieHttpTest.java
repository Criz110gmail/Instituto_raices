package escuela.config;

import escuela.inicio.controller.LoginController;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.config.annotation.*;
import static org.assertj.core.api.Assertions.*;

/** Cookies HTTP de Tomcat reales; cuentas únicamente en memoria, sin BD ni .env. */
class LoginCookieHttpTest {
    @Test void logoutYPrimerLoginConCookiesReales() throws Exception {
        var tomcat=new Tomcat();
        Path base=Path.of("target/qa-login-http");Files.createDirectories(base);
        tomcat.setBaseDir(base.toAbsolutePath().toString());tomcat.setPort(Boolean.getBoolean("qa.browser")?18082:0);
        var connector=tomcat.getConnector();connector.setProperty("address","0.0.0.0");
        var ctx=tomcat.addContext("",base.toAbsolutePath().toString());
        try(var spring=new AnnotationConfigWebApplicationContext()) {
            spring.setServletContext(ctx.getServletContext());
            spring.register(LoginReinicioSeguridadTest.Config.class,SeguridadConfig.class,
                    SesionExpiradaAccessDeniedHandler.class,SesionController.class,LoginController.class,
                    LoginRenderSinSesionTest.Vistas.class,Recursos.class,Paginas.class);
            spring.refresh();
            Tomcat.addServlet(ctx,"mvc",new DispatcherServlet(spring)).setLoadOnStartup(1);ctx.addServletMappingDecoded("/","mvc");
            var filtro=new FilterDef();filtro.setFilterName("security");filtro.setFilter(new DelegatingFilterProxy("springSecurityFilterChain",spring));ctx.addFilterDef(filtro);
            var mapa=new FilterMap();mapa.setFilterName("security");mapa.addURLPattern("/*");ctx.addFilterMap(mapa);
            tomcat.start();
            if(Boolean.getBoolean("qa.browser")) {
                System.out.println("QA_LOGIN_HTTP_LISTO");
                new CountDownLatch(1).await(10,java.util.concurrent.TimeUnit.MINUTES);
                return;
            }
            var cookies=new CookieManager(null,CookiePolicy.ACCEPT_ALL);
            var http=HttpClient.newBuilder().cookieHandler(cookies).followRedirects(HttpClient.Redirect.NEVER).connectTimeout(Duration.ofSeconds(5)).build();
            String host="http://localhost:"+connector.getLocalPort();
            for(String origen:new String[]{"admin","familias","maestros"})for(int ciclo=0;ciclo<3;ciclo++) {
                String acceso=origen.equals("admin")?"/login":origen.equals("familias")?"/familias":"/maestros/acceso";
                var pagina=http.send(HttpRequest.newBuilder(URI.create(host+acceso)).GET().build(),HttpResponse.BodyHandlers.ofString());
                assertThat(pagina.statusCode()).isEqualTo(200);
                String token=token(http,host);
                assertThat(post(http,host,"/acceso/comprobar","_csrf="+URLEncoder.encode(token,java.nio.charset.StandardCharsets.UTF_8)).statusCode()).isEqualTo(204);
                var entrada=post(http,host,"/login","_csrf="+URLEncoder.encode(token,java.nio.charset.StandardCharsets.UTF_8)+"&origen="+origen+"&username=usuario-qa&password=clave-qa");
                assertThat(entrada.headers().firstValue("location").orElse("")).endsWith(origen.equals("admin")?"/admin":origen.equals("familias")?"/portal":"/maestros");
                var privada=http.send(HttpRequest.newBuilder(URI.create(host+"/admin")).GET().build(),HttpResponse.BodyHandlers.ofString());
                assertThat(privada.statusCode()).isEqualTo(200);
                var salida=post(http,host,"/logout","_csrf="+URLEncoder.encode(token(http,host),java.nio.charset.StandardCharsets.UTF_8)+"&origen="+origen);
                assertThat(salida.headers().firstValue("location").orElse("")).endsWith(acceso+"?logout");
            }
        } finally {tomcat.stop();tomcat.destroy();}
    }
    private String token(HttpClient h,String host) throws Exception {
        var respuesta=h.send(HttpRequest.newBuilder(URI.create(host+"/acceso/csrf")).GET().build(),HttpResponse.BodyHandlers.ofString());
        assertThat(respuesta.statusCode()).isEqualTo(200);
        var m=java.util.regex.Pattern.compile("\"token\":\"([^\"]+)\"").matcher(respuesta.body());assertThat(m.find()).isTrue();return m.group(1);
    }
    private HttpResponse<String> post(HttpClient h,String host,String ruta,String cuerpo) throws Exception {
        return h.send(HttpRequest.newBuilder(URI.create(host+ruta)).header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(cuerpo)).build(),HttpResponse.BodyHandlers.ofString());
    }
    @Configuration static class Recursos implements WebMvcConfigurer {
        @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/js/**").addResourceLocations("classpath:/static/js/");
            registry.addResourceHandler("/css/**").addResourceLocations("classpath:/static/css/");
        }
    }
    @org.springframework.stereotype.Controller static class Paginas {
        @GetMapping({"/admin","/portal","/maestros"}) String privada(org.springframework.ui.Model modelo) {
            modelo.addAttribute("usuarioSesion","usuario-qa");return "qa/login-privado";
        }
    }
}
