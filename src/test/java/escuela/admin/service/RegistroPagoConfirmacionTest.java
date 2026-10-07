package escuela.admin.service;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class RegistroPagoConfirmacionTest {
 @Test void formularioCompartidoConfirmaAltaSinCambiarMultipartNiCsrf() throws Exception {
  String html=Files.readString(Path.of("src/main/resources/templates/admin/pago-form.html"));
  assertThat(html).contains("data-money-confirm=\"registro-pago\"", "th:action=\"@{/admin/pagos}\"", "enctype=\"multipart/form-data\"");
  String js=Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
  assertThat(js).contains("Confirmar registro del pago", "Confirmar y registrar como pendiente", "Distribuido entre cargos", "Comprobantes seleccionados", "pendiente de validación", "distributed = 0n");
  assertThat(js).doesNotContain("innerHTML", "form.submit()");
 }
 @Test void guiaSoloActualizaTextoYVersion() throws Exception {
  String sql=Files.readString(Path.of("src/main/resources/db/migration/V71__guias_confirmacion_registro_pago.sql"));
  assertThat(sql).contains("UPDATE guia_proceso_paso", "Confirmar y registrar como pendiente", "UPDATE guia_proceso").doesNotContain("UPDATE pago", "INSERT INTO", "ALTER TABLE");
 }
}
