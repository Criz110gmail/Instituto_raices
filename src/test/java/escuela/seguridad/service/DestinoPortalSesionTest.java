package escuela.seguridad.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class DestinoPortalSesionTest {
 @Test void elDestinoDependeDelPortalAutorizadoSinModificarLaAutenticacion(){
  var docente=new TestingAuthenticationToken("qa","", "PORTAL_MAESTRO_ACCEDER");
  assertThat(DestinoPortalSesion.inicio(docente)).isEqualTo("/maestros");
  assertThat(docente.getAuthorities()).extracting("authority").containsExactly("PORTAL_MAESTRO_ACCEDER");
  assertThat(DestinoPortalSesion.inicio(new TestingAuthenticationToken("qa","","PORTAL_TUTOR_ACCEDER"))).isEqualTo("/portal");
  assertThat(DestinoPortalSesion.inicio(new TestingAuthenticationToken("qa","","USUARIO_ADMINISTRAR"))).isEqualTo("/admin");
  assertThat(DestinoPortalSesion.inicio(new TestingAuthenticationToken("qa","","PORTAL_TUTOR_ACCEDER","PORTAL_MAESTRO_ACCEDER"))).isEqualTo("/admin");
  assertThat(DestinoPortalSesion.inicio(null)).isEqualTo("/");
  assertThat(DestinoPortalSesion.inicio(new AnonymousAuthenticationToken("qa","anónimo",List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))))).isEqualTo("/");
 }
}
