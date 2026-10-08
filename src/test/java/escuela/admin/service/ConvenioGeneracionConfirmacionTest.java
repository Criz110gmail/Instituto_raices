package escuela.admin.service;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class ConvenioGeneracionConfirmacionTest {
 @Test void confirmaGeneracionSinBloquearBotonAntesDelModal() throws Exception {
  String html=Files.readString(Path.of("src/main/resources/templates/admin/cargo-generar.html"));
  assertThat(html).contains("data-money-confirm=\"generacion-cargos\"", "/js/money-input.js", "data-selection-confirm", "Cancelar generación");
  String js=Files.readString(Path.of("src/main/resources/static/js/generation-selection.js"));
  assertThat(js).contains("form.dataset.moneyConfirm&&!event.moneyConfirmed", "inputs();return;", "if(event.defaultPrevented)return");
 }
 @Test void separaBusquedaYSeleccionYConfirmaConvenio() throws Exception {
  String html=Files.readString(Path.of("src/main/resources/templates/admin/convenio-pago-form.html"));
  assertThat(html).contains("data-money-confirm=\"convenio\"", "Resultados disponibles", "Cargos agregados al convenio", "selected-zone", "seleccion-resumen", "seleccion-vacia");
  String css=Files.readString(Path.of("src/main/resources/static/css/convenios.css"));
  assertThat(css).contains(".agreement-charge-zone.selected-zone", "[data-theme=dark] .agreement-charge-zone.selected-zone", "background:var(--surface)");
  String js=Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
  assertThat(js).contains("Confirmar convenio de pago", "Confirmar y crear convenio", "Confirmar generación de adeudos", "Selección de todas las páginas");
 }
 @Test void guiaSoloActualizaTextoYVersion() throws Exception {
  String sql=Files.readString(Path.of("src/main/resources/db/migration/V75__guias_modal_generacion_adeudos.sql"));
  assertThat(sql).contains("UPDATE guia_proceso_paso", "UPDATE guia_proceso", "Confirmar y generar adeudos").doesNotContain("INSERT INTO", "UPDATE cargo", "ALTER TABLE");
 }
}
