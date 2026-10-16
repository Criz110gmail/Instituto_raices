package escuela.config;

import escuela.admin.controller.ReporteFinancieroAdminController;
import escuela.admin.dto.*;
import escuela.admin.service.*;
import escuela.common.exception.ManejadorGlobalExcepciones;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.*;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Detecta getWriter/getOutputStream con Tomcat real, no sólo MockMvc. Sin BD. */
class ReportesExportacionDenegadaHttpTest {
    @Test void ochoExportacionesRechazadasMuestranHtml403YLasAutorizadasConservanArchivo() throws Exception {
        var tomcat=new Tomcat();var base=Path.of("target/qa-reportes-http");Files.createDirectories(base);
        tomcat.setBaseDir(base.toAbsolutePath().toString());tomcat.setPort(0);var connector=tomcat.getConnector();
        var ctx=tomcat.addContext("",base.toAbsolutePath().toString());
        try(var spring=new AnnotationConfigWebApplicationContext()) {
            spring.setServletContext(ctx.getServletContext());
            spring.register(Config.class,SeguridadConfig.class,SesionExpiradaAccessDeniedHandler.class,
                    ReporteFinancieroAdminController.class,ManejadorGlobalExcepciones.class,LoginRenderSinSesionTest.Vistas.class);
            spring.refresh();
            Tomcat.addServlet(ctx,"mvc",new DispatcherServlet(spring)).setLoadOnStartup(1);ctx.addServletMappingDecoded("/","mvc");
            var def=new FilterDef();def.setFilterName("security");def.setFilter(new DelegatingFilterProxy("springSecurityFilterChain",spring));ctx.addFilterDef(def);
            var map=new FilterMap();map.setFilterName("security");map.addURLPattern("/*");ctx.addFilterMap(map);tomcat.start();
            var http=HttpClient.newHttpClient();String baseUrl="http://localhost:"+connector.getLocalPort()+"/admin/reportes-financieros/";
            String credencial=Base64.getEncoder().encodeToString("qa:clave-ficticia".getBytes(java.nio.charset.StandardCharsets.UTF_8));
            for(boolean denegar:new boolean[]{true,false}) {
                spring.getBean(AtomicBoolean.class).set(denegar);
                for(String reporte:List.of("tesoreria","estado-cuenta","estado-cuenta-cuenta","concentrado-cobranza"))for(String tipo:List.of("excel","pdf")) {
                    var uri=URI.create(baseUrl+reporte+"/"+tipo+"?institucionId=1&plantelId=2&alumnoId=1&cuentaId=1&anio=2026&mes=10&fechaDesde=2026-10-01&fechaHasta=2026-10-16&fechaCorte=2026-10-16");
                    var respuesta=http.send(HttpRequest.newBuilder(uri).header("Authorization","Basic "+credencial).GET().build(),HttpResponse.BodyHandlers.ofString());
                    assertThat(respuesta.statusCode()).as(reporte+"/"+tipo+" denegado="+denegar).isEqualTo(denegar?403:200);
                    if(denegar) {
                        assertThat(respuesta.headers().firstValue("content-type").orElse("")).startsWith("text/html");
                        assertThat(respuesta.headers().firstValue("content-disposition")).isEmpty();
                        assertThat(respuesta.body()).contains("No tienes acceso a esta información").doesNotContain("Whitelabel");
                    } else {
                        assertThat(respuesta.headers().firstValue("content-type").orElse("")).startsWith(tipo.equals("pdf")?"application/pdf":"application/vnd.openxmlformats");
                        assertThat(respuesta.headers().firstValue("content-disposition").orElse("")).startsWith(tipo.equals("pdf")?"inline;":"attachment;");
                        assertThat(respuesta.body()).isEqualTo("archivo-ficticio");
                    }
                }
            }
        } finally {tomcat.stop();tomcat.destroy();}
    }
    @Configuration @EnableWebSecurity @EnableWebMvc static class Config {
        @Bean AtomicBoolean denegar(){return new AtomicBoolean(true);}
        @Bean UserDetailsService usuarios(){return new InMemoryUserDetailsManager(User.withUsername("qa").password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("clave-ficticia")).authorities("REPORTE_FINANCIERO_CONSULTAR").build());}
        private org.mockito.stubbing.Answer<Void> exportar(AtomicBoolean denegar){return i->{
            if(denegar.get())throw new AccessDeniedException("Plantel fuera del alcance");
            ((OutputStream)i.getArgument(1)).write("archivo-ficticio".getBytes(java.nio.charset.StandardCharsets.UTF_8));return null;
        };}
        @Bean ReporteFinancieroConsultaService consulta(){var s=mock(ReporteFinancieroConsultaService.class);
            when(s.normalizar(any(FiltroConcentradoCobranza.class))).thenAnswer(i->i.getArgument(0));
            when(s.normalizar(any(FiltroReporteTesoreria.class))).thenAnswer(i->i.getArgument(0));
            when(s.normalizar(any(FiltroEstadoCuentaAlumno.class))).thenAnswer(i->i.getArgument(0));return s;}
        @Bean EstadoCuentaCuentaService cuenta(){var s=mock(EstadoCuentaCuentaService.class);when(s.normalizar(any())).thenAnswer(i->i.getArgument(0));return s;}
        @Bean ExcelReporteFinancieroService excel(AtomicBoolean d) throws Exception {var s=mock(ExcelReporteFinancieroService.class);doAnswer(exportar(d)).when(s).tesoreria(any(),any());doAnswer(exportar(d)).when(s).estadoCuenta(any(),any());return s;}
        @Bean ExcelConcentradoCobranzaService concentrado(AtomicBoolean d) throws Exception {var s=mock(ExcelConcentradoCobranzaService.class);doAnswer(exportar(d)).when(s).exportar(any(),any());return s;}
        @Bean ExcelEstadoCuentaCuentaService excelCuenta(AtomicBoolean d) throws Exception {var s=mock(ExcelEstadoCuentaCuentaService.class);doAnswer(exportar(d)).when(s).exportar(any(),any());return s;}
        @Bean JasperReporteFinancieroService jasper(AtomicBoolean d){var s=mock(JasperReporteFinancieroService.class);doAnswer(exportar(d)).when(s).tesoreria(any(),any());doAnswer(exportar(d)).when(s).estadoCuenta(any(),any());doAnswer(exportar(d)).when(s).estadoAlumno(any(),any());doAnswer(exportar(d)).when(s).concentradoCobranza(any(),any());return s;}
        @Bean InstitucionService instituciones(){return mock(InstitucionService.class);}
        @Bean PlantelService planteles(){return mock(PlantelService.class);}
        @Bean AlcanceDatosService alcance(){return mock(AlcanceDatosService.class);}
    }
}
