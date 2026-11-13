package escuela.config;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.*;
import jakarta.servlet.http.HttpServletRequest;
import static org.assertj.core.api.Assertions.*;
class InstitucionAccesoFilterTest {
 @Test void elUsuarioSeCalificaSinCambiarCredencialesNiElegirOtraInstitucion()throws Exception{for(String name:new String[]{"persona","OTRA\\persona","QA\\persona"}){var r=new MockHttpServletRequest("POST","/login");r.setParameter("username",name);r.setParameter("password","ficticia");r.setParameter("institucionAcceso","QA");new InstitucionAccesoFilter().doFilter(r,new MockHttpServletResponse(),(req,res)->{var q=(HttpServletRequest)req;assertThat(q.getParameter("username")).isEqualTo(name.equals("OTRA\\persona")?"QA\\OTRA\\persona":"QA\\persona");assertThat(q.getParameter("password")).isEqualTo("ficticia");});}}
 @Test void enlacesDeRetornoNoAdmitenUrlsNiCreanSesion() {var r=new MockHttpServletRequest();r.setParameter("institucionAcceso","https://evil.test");assertThat(InstitucionAccesoFilter.continuar(r,"/login?error")).isEqualTo("/login?error");r.setParameter("institucionAcceso","QA-1");assertThat(InstitucionAccesoFilter.continuar(r,"/login?error")).isEqualTo("/login?error&institucion=QA-1");assertThat(r.getSession(false)).isNull();}
}
