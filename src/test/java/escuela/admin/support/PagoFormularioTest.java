package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PagoFormularioTest {
    @Test
    void formularioNuevoToleraAusenciaDeErrorEIncluyeCsrfEnElEnvio() throws IOException {
        String plantilla;
        try (var entrada = getClass().getClassLoader()
                .getResourceAsStream("templates/admin/pago-form.html")) {
            assertThat(entrada).isNotNull();
            plantilla = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertThat(plantilla)
                .contains("method=\"post\" th:action=\"@{/admin/pagos}\"")
                .contains("errorOperacion != null or #fields.hasErrors('*')")
                .contains("'Tarjeta'", "th:readonly=\"${pagoRapido}\"",
                        "protected-payment-field", "payment-submit-button",
                        "partial-payment-toggle", "Registrar pago parcial",
                        "data-fixed-charge", "monto-solicitado",
                        "full-amount-reference", "saldoReferencia",
                        "input-help", "field-error", "Cuenta declarada *",
                        "th:errors=\"*{cuentaDeclaradaId}\"")
                .doesNotContain("th:field=\"*{folio}\"")
                .doesNotContain("${errorOperacion or #fields.hasErrors('*')}");
    }
}
