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
                        "Total recibido", "Distribuido entre cargos",
                        "Dinero pendiente de asignar",
                        "th:errors=\"*{cuentaDeclaradaId}\"")
                .doesNotContain("th:field=\"*{folio}\"")
                .doesNotContain("${errorOperacion or #fields.hasErrors('*')}");

        String detalle;
        try (var entrada = getClass().getClassLoader()
                .getResourceAsStream("templates/admin/pago-detalle.html")) {
            assertThat(entrada).isNotNull();
            detalle = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertThat(detalle).contains("Saldo actual del cargo", "Importe solicitado para el cargo",
                "El importe reportado es diferente del saldo actual",
                "Seguirá debiendo después de validar", "saldoEstimadoTrasValidacion");
    }
}
