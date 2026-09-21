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
                .doesNotContain("${errorOperacion or #fields.hasErrors('*')}");
    }
}
