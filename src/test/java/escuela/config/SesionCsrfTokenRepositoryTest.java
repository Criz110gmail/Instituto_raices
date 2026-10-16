package escuela.config;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import static org.assertj.core.api.Assertions.*;

class SesionCsrfTokenRepositoryTest {
    private final SesionCsrfTokenRepository repositorio=new SesionCsrfTokenRepository();
    @Test void soloAccesosQueNecesitanCsrfPuedenCrearSesionAnonima() {
        for(String ruta:new String[]{"/acceso/csrf","/activar-cuenta","/restablecer-password"}) {
            var request=new MockHttpServletRequest("GET",ruta);
            var token=repositorio.generateToken(request);
            repositorio.saveToken(token,request,new MockHttpServletResponse());
            assertThat(request.getSession(false)).isNotNull();assertThat(repositorio.loadToken(request)).isSameAs(token);
        }
    }
    @Test void privadosYLoginsNoResucitanSesionesNiAlResolverTokenDiferido() {
        for(String ruta:new String[]{"/admin/usuarios/1/editar","/portal","/maestros","/sesion/estado","/login","/familias","/maestros/acceso"}) {
            var request=new MockHttpServletRequest("GET",ruta);
            var anterior=new MockHttpSession();request.setSession(anterior);
            var diferido=repositorio.loadDeferredToken(request,new MockHttpServletResponse());
            anterior.invalidate();assertThat(diferido.get()).isNotNull();
            assertThat(request.getSession(false)).isNull();
        }
    }
    @Test void sesionExistenteConservaTokenYLaRevocacionNoCreaOtra() {
        var request=new MockHttpServletRequest("GET","/admin");request.setSession(new MockHttpSession());
        var token=repositorio.generateToken(request);repositorio.saveToken(token,request,new MockHttpServletResponse());
        assertThat(repositorio.loadToken(request)).isSameAs(token);
        repositorio.saveToken(null,request,new MockHttpServletResponse());assertThat(repositorio.loadToken(request)).isNull();
        request.getSession(false).invalidate();repositorio.saveToken(null,request,new MockHttpServletResponse());
        assertThat(request.getSession(false)).isNull();
    }
    @Test void postSinTokenNoPuedePrepararUnaSesionNueva() {
        var request=new MockHttpServletRequest("POST","/login");
        repositorio.loadDeferredToken(request,new MockHttpServletResponse()).get();
        assertThat(request.getSession(false)).isNull();
    }
}
