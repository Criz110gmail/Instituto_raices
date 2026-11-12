package escuela.sitioweb;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class SitioWhatsappTest {
    @Test void enlaceSoloAdmiteDestinoWhatsappYMensajeCodificado(){
        assertThat(SitioWhatsapp.numero("+52 (33) 1234-5678")).isEqualTo("523312345678");
        for(var phone:java.util.List.of("", "https://evil.test/523312345678","javascript:523312345678","00012345678","123","+52 ext 1234567890","1".repeat(16)))assertThat(SitioWhatsapp.url(phone,"Hola")).isEmpty();
        assertThat(SitioWhatsapp.url("523312345678","Hola & información\n<seguro>")).isEqualTo("https://wa.me/523312345678?text=Hola+%26+informaci%C3%B3n%0A%3Cseguro%3E");
    }
    @Test void publicarExigeNumeroYNoAdmiteEnlacesNiHijos(){
        var d=new SitioDiseno();var b=new SitioDiseno.Bloque();b.setId("contacto");b.setTipo("seccion");var n=new SitioComponente();n.setId("whatsapp-prueba");n.setTipo("whatsapp");n.setTag("a");b.getComponentes().add(n);d.getBloques().add(b);
        assertThatCode(()->d.validar(false)).doesNotThrowAnyException();assertThatThrownBy(()->d.validar(true)).hasMessageContaining("número de WhatsApp");
        n.setWhatsappNumero("+52 33 1234 5678");assertThatCode(()->d.validar(true)).doesNotThrowAnyException();n.setEnlace("https://evil.test");assertThatThrownBy(()->d.validar(true)).hasMessageContaining("número y mensaje");n.setEnlace("");n.setTag("div");assertThatThrownBy(()->d.validar(true)).hasMessageContaining("número y mensaje");
    }
    @Test void renderPublicoEscapaEtiquetaYConservaDestinoSeguro(){
        var d=new SitioDiseno();var b=new SitioDiseno.Bloque();b.setId("contacto");b.setTipo("seccion");var n=new SitioComponente();n.setId("whatsapp-prueba");n.setTipo("whatsapp");n.setTag("a");n.setTexto("Información <segura>");n.setWhatsappNumero("523312345678");n.setWhatsappMensaje("Hola & horarios");n.setWhatsappDiseno("icono");b.getComponentes().add(n);d.getBloques().add(b);var f=new SitioForm();f.setDiseno(d);
        var html=new SitioDisenadorRender().documento(f,"/imagenes/");assertThat(html).contains("nd-whatsapp-button","href=\"https://wa.me/523312345678?text=Hola+%26+horarios\"","data-whatsapp-layout=\"icono\"","rel=\"noopener noreferrer\"","target=\"_blank\"","aria-label=\"Información &lt;segura&gt;\"").doesNotContain("<segura>");
        n.setWhatsappNumero("");assertThat(new SitioDisenadorRender().documento(f,"/imagenes/")).contains("aria-disabled=\"true\"").doesNotContain("href=\"https://wa.me");
    }
}
