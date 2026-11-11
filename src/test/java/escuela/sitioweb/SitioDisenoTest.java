package escuela.sitioweb;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class SitioDisenoTest {
 SitioDiseno diseno(){var d=new SitioDiseno();var b=new SitioDiseno.Bloque();b.setId("bloque-1");b.setTipo("portada");b.setTitulo("Escuela");d.getBloques().add(b);return d;}
 @Test void rechazaEnlacesEjecutablesYCredenciales(){for(var url:java.util.List.of("javascript:alert(1)","data:text/html,hola","//externo.test","https://usuario:clave@example.org","/\\externo"))assertThatThrownBy(()->SitioDiseno.validarEnlace(url)).isInstanceOf(escuela.common.exception.ReglaNegocioException.class);}
 @Test void aceptaDestinosExplicitos(){for(var url:java.util.List.of("https://example.org/info","#b-bloque-1","/familias","mailto:info@example.org","tel:+525555555555",""))assertThatCode(()->SitioDiseno.validarEnlace(url)).doesNotThrowAnyException();}
 @Test void permiteBorradorIncompletoPeroNoPublicarlo(){var d=diseno();var imagen=new SitioDiseno.Bloque();imagen.setId("imagen-1");imagen.setTipo("imagen");d.getBloques().add(imagen);assertThatCode(()->d.validar(false)).doesNotThrowAnyException();assertThatThrownBy(()->d.validar(true)).hasMessageContaining("Selecciona la imagen");}
 @Test void identificaBloquesDuplicadosYContraste(){var d=diseno();d.getBloques().add(d.getBloques().getFirst());assertThatThrownBy(()->d.validar(false)).hasMessageContaining("identificador");var otro=diseno();otro.getBloques().getFirst().setColor("#ffffff");assertThatThrownBy(()->otro.validar(false)).hasMessageContaining("contraste");}
 @Test void noSerializaPropiedadesDePresentacion(){var d=diseno();var mapper=tools.jackson.databind.json.JsonMapper.builder().build();var json=mapper.writeValueAsString(d);assertThat(json).doesNotContain("estilo","lineas","tarjetas");assertThat(mapper.readValue(json,SitioDiseno.class).getBloques()).hasSize(1);}
 @Test void limitaCantidadYEstilosConBeanValidation(){try(var f=jakarta.validation.Validation.buildDefaultValidatorFactory()){var d=diseno();d.setFuente("url(javascript:alert(1))");assertThat(f.getValidator().validate(d)).isNotEmpty();d.setFuente("moderna");d.getBloques().getFirst().setFondo("red;background-image:url(https://example.org)");assertThat(f.getValidator().validate(d)).isNotEmpty();}}
}
