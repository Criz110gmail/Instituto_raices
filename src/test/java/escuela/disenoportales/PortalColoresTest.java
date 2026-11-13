package escuela.disenoportales;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class PortalColoresTest {
 @Test void cualquierColorHexadecimalConservaContrasteLegible(){for(int r=0;r<256;r+=51)for(int g=0;g<256;g+=51)for(int b=0;b<256;b+=51){String c=String.format("#%02x%02x%02x",r,g,b);double a=PortalColores.luminancia(c),v=PortalColores.luminancia(PortalColores.texto(c));assertThat((Math.max(a,v)+.05)/(Math.min(a,v)+.05)).isGreaterThanOrEqualTo(4.5);}assertThat(PortalColores.variables("javascript:alert(1)","#ffffff","#123456")).isEmpty();}
 @Test void soloExistenCuatroPortalesDeDestinoFijo(){assertThat(PortalTipo.values()).hasSize(4);assertThat(PortalTipo.de("personal").plantilla).isEqualTo("login-personal");assertThatThrownBy(()->PortalTipo.de("https://otro.test")).isInstanceOf(org.springframework.web.server.ResponseStatusException.class);}
}
