package escuela.sitioweb;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class SitioExtrasTest {
 @Test void datosDerivadosNoSeGuardanEnSnapshot(){var extras=new SitioExtras();extras.getContacto().setWhatsappActivo(true);extras.getContacto().setWhatsapp("525555555555");extras.getContacto().setMensaje("Hola & adiós");var album=new SitioExtras.Album();album.setTitulo("Prueba");extras.getAlbumes().add(album);var mapper=tools.jackson.databind.json.JsonMapper.builder().build();var json=mapper.writeValueAsString(extras);assertThat(json).doesNotContain("whatsappUrl","telefonoUrl","fotosPublicas","mapaUrl");assertThat(mapper.readValue(json,SitioExtras.class).getContacto().getWhatsappUrl()).contains("%26");}
 @Test void whatsappDesactivadoNoProduceEnlace(){var c=new SitioExtras.Contacto();c.setWhatsapp("525555555555");assertThat(c.getWhatsappUrl()).isNull();}
}
