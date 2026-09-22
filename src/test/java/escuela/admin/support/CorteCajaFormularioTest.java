package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CorteCajaFormularioTest {
    @Test
    void aperturaYCierreProcesanLaAccionParaIncluirCsrf() throws IOException {
        assertThat(plantilla("admin/corte-caja-apertura-form.html"))
                .contains("method=\"post\" th:action=\"@{/admin/cortes-caja}\"")
                .doesNotContain("method=\"post\" action=\"/admin/cortes-caja\"");
        assertThat(plantilla("admin/corte-caja-detalle.html"))
                .contains("method=\"post\" th:action=\"@{/admin/cortes-caja/{id}/cerrar(id=${corte.id})}\"");
    }

    private String plantilla(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream("templates/" + ruta)) {
            assertThat(entrada).as("plantilla %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
