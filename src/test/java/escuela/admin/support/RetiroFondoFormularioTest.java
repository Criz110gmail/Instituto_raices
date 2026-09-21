package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RetiroFondoFormularioTest {
    @Test
    void formularioPostProcesaLaAccionParaIncluirCsrfYMarcaElErrorDelAutocompletado() throws IOException {
        String plantilla;
        try (var entrada = getClass().getClassLoader()
                .getResourceAsStream("templates/admin/retiro-fondo-form.html")) {
            assertThat(entrada).isNotNull();
            plantilla = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
        assertThat(plantilla).contains("method=\"post\" th:action=\"@{/admin/retiros-fondo}\"")
                .contains("data-autocomplete-error th:errors=\"*{cuentaId}\"");
    }
}
