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

    @Test
    void capturaComparteElDisenoAcademicoResponsivo() throws IOException {
        assertThat(plantilla("admin/calificacion-captura.html"))
                .contains("academic-capture-page", "academic-capture-card",
                        "academic-capture-intro", "capture-selector-section",
                        "capture-workspace", "gradebook-table-wrap");
        assertThat(recurso("static/css/forms.css"))
                .contains("Experiencia compartida para capturas académicas",
                        ".academic-capture-intro", ".capture-selector-section",
                        ".capture-workspace .gradebook-table",
                        "content: attr(data-label)",
                        "[data-theme=dark] .academic-capture-card",
                        "@media (max-width: 560px)");
    }

    private String plantilla(String ruta) throws IOException {
        return recurso("templates/" + ruta);
    }

    private String recurso(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).as("plantilla %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
