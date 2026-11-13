package escuela.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class SesionInvalidaFilterTest {
    @AfterEach void limpiar(){SecurityContextHolder.clearContext();}
    @Test void unaCookieAnteriorNoBloqueaLasPaginasNiImagenesPublicadas() throws Exception {
        for(String ruta:List.of("/","/sitio/INSTITUCION_DEMO","/sitio/INSTITUCION_DEMO/imagenes/5","/images/logo.png","/images/imagen.png","/images/sitio-objetos-escolares.svg")){
            var request=new MockHttpServletRequest("GET",ruta);request.setRequestedSessionId("anterior");request.setRequestedSessionIdValid(false);
            var response=new MockHttpServletResponse();var chain=new MockFilterChain();
            new SesionInvalidaFilter(new SesionExpiradaAccessDeniedHandler()).doFilter(request,response,chain);
            assertThat(chain.getRequest()).as(ruta).isSameAs(request);assertThat(response.getRedirectedUrl()).isNull();assertThat(request.getSession(false)).isNull();
        }
    }
    @Test void noConvierteEnPublicosElEditorLosArchivosPrivadosNiLasOperaciones() throws Exception {
        for(String ruta:List.of("/admin/diseno-web","/admin/diseno-web/imagenes/5","/sitio/DEMO/editar","/images/privado.png")){
            var r=new MockHttpServletRequest("GET",ruta);r.setRequestedSessionId("anterior");r.setRequestedSessionIdValid(false);var response=new MockHttpServletResponse();var chain=new MockFilterChain();
            new SesionInvalidaFilter(new SesionExpiradaAccessDeniedHandler()).doFilter(r,response,chain);assertThat(chain.getRequest()).isNull();assertThat(response.getRedirectedUrl()).isNotNull();
        }
        assertThat(SesionInvalidaFilter.consultaPublica(new MockHttpServletRequest("POST","/sitio/DEMO"))).isFalse();
    }
}
