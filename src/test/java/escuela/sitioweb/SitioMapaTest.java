package escuela.sitioweb;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SitioMapaTest {
    @Test void direccionSeCodificaYLaHtmlSeEscapaSinAceptarEmbedsExternos(){
        String address="Parián 1974 & <script>alert(1)</script>";
        assertThat(SitioMapa.embed(address,99)).startsWith("https://maps.google.com/maps?q=Pari%C3%A1n+1974").contains("%3Cscript%3E","&z=19");
        String html=SitioMapa.html("Visítanos <seguro>",address,16,"tarjeta",""," ");
        assertThat(html).contains("&lt;seguro&gt;","&lt;script&gt;","loading=\"lazy\"","strict-origin-when-cross-origin","https://www.google.com/maps/dir/?api=1&amp;destination=").doesNotContain("<script>");
    }
    @Test void mapaVacioEsUnEstadoDeCapturaYNoSePuedePublicar(){
        var d=new SitioDiseno();var section=new SitioDiseno.Bloque();section.setId("ubicacion-prueba");section.setTipo("seccion");section.setTitulo("Ubicaciones");var n=new SitioComponente();n.setId("mapa-prueba");n.setTipo("mapa");n.setTexto("Ven a conocernos");section.getComponentes().add(n);d.getBloques().add(section);
        assertThatCode(()->d.validar(false)).doesNotThrowAnyException();assertThatThrownBy(()->d.validar(true)).hasMessageContaining("dirección del mapa");
        n.setDireccionMapa("Parián 1974, Guadalajara");assertThatCode(()->d.validar(true)).doesNotThrowAnyException();var f=new SitioForm();f.setDiseno(d);String html=new SitioDisenadorRender().documento(f,"/imagenes/");assertThat(html).contains("data-component-type=\"mapa\"","nd-map-card","<iframe","Parián 1974");assertThat(html.split("Ven a conocernos",-1)).hasSize(2);
        n.setZoomMapa(30);n.setDisenoMapa("<script>");try(var factory=jakarta.validation.Validation.buildDefaultValidatorFactory()){assertThat(factory.getValidator().validate(d)).hasSizeGreaterThanOrEqualTo(2);}
    }
}
