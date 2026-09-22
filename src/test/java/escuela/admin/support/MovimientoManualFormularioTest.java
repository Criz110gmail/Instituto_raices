package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MovimientoManualFormularioTest {
    @Test
    void registrarMovimientoProcesaLaAccionParaIncluirCsrf() throws IOException {
        try (var entrada = getClass().getClassLoader()
                .getResourceAsStream("templates/admin/movimiento-manual-form.html")) {
            assertThat(entrada).isNotNull();
            String plantilla = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(plantilla)
                    .contains("method=\"post\" th:action=\"@{/admin/movimientos-financieros}\"")
                    .doesNotContain("method=\"post\" action=\"/admin/movimientos-financieros\"");
        }
    }
}
