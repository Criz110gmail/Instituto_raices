package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CalificacionFormularioTest {
    @Test
    void accionesDeCalificacionProcesanLaRutaParaIncluirCsrf() throws IOException {
        assertThat(plantilla("admin/calificacion-captura.html"))
                .contains("method=\"post\" th:action=\"@{/admin/calificaciones/captura}\"")
                .contains("method=\"post\" th:action=\"@{/admin/calificaciones/reabrir}\"");
    }

    private String plantilla(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream("templates/" + ruta)) {
            assertThat(entrada).as("plantilla %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
