package escuela.admin.support;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.assertThat;
class ConvenioPagoInterfazTest {
 @Test void verTieneBotonComunYErroresAbrenGestion()throws Exception{
  String listado=Files.readString(Path.of("src/main/resources/templates/admin/convenios-pago.html"));
  assertThat(listado).contains("class=\"new-button\" th:href=\"@{/admin/convenios-pago/{id}");
  String controller=Files.readString(Path.of("src/main/java/escuela/admin/controller/ConvenioPagoAdminController.java"));
  assertThat(controller).contains("m.addAttribute(\"pestanaActiva\",\"gestion\")", "flash.addFlashAttribute(\"pestanaActiva\",\"gestion\")");
 }
 @Test void buscadorDeCargosReutilizaInputsComunesEnAmbosTemas()throws Exception{
  String html=Files.readString(Path.of("src/main/resources/templates/admin/convenio-pago-form.html"));
  String css=Files.readString(Path.of("src/main/resources/static/css/admin.css"));
  assertThat(html).contains("class=\"form-grid agreement-charge-search\"");
  assertThat(css).contains(".form-grid input:not([type=checkbox])", "[data-theme=dark] .form-grid input", ".form-grid input:focus");
  assertThat(Files.readString(Path.of("src/main/resources/static/css/convenios.css")))
    .contains(".agreement-charge-search{grid-template-columns:1fr}");
 }
 @Test void permiteBuscarCargosSinCargarCatalogosMasivos()throws Exception{String html=Files.readString(Path.of("src/main/resources/templates/admin/convenio-pago-form.html"));String js=Files.readString(Path.of("src/main/resources/static/js/convenio-pago-form.js"));assertThat(html).contains("cargo-busqueda","cargoIds", "class=\"form-intro finance-intro\"", "class=\"entity-form\"", "class=\"form-section\"");assertThat(js).contains("URLSearchParams","/admin/convenios-pago/cargos-disponibles");}
}
