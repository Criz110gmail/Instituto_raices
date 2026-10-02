package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CargoFormularioTest {
    @Test
    void formulariosDeCargoProcesanLaAccionParaIncluirCsrf() throws IOException {
        assertThat(plantilla("admin/cargo-form.html"))
                .contains("method=\"post\" th:action=\"@{/admin/cargos}\"")
                .contains("Esta descripción se conservará en el registro")
                .contains("Registrar pago por cobrar");
        assertThat(plantilla("admin/cargo-generar.html"))
                .contains("Visualizar cuotas por aplicar")
                .contains("th:action=\"@{/admin/cargos/generar/vista-previa}\"")
                .contains("Confirmar y generar pagos faltantes")
                .contains("method=\"post\" th:action=\"@{/admin/cargos/generar}\"")
                .contains("data-preview-form", "data-generation-preview",
                        "/js/generation-preview.js");
    }

    private String plantilla(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream("templates/" + ruta)) {
            assertThat(entrada).as("plantilla %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
