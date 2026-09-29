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

    @Test
    void publicarYReabrirUsanModalPropioAccesible() throws IOException {
        String html = plantilla("admin/calificacion-captura.html");
        String javascript = recurso("static/js/gradebook-confirm.js");
        String estilos = recurso("static/css/forms.css");

        assertThat(html)
                .doesNotContain("return confirm(")
                .contains("data-gradebook-confirm=\"publicar\"",
                        "data-gradebook-confirm=\"reabrir\"",
                        "data-gradebook-confirm-modal", "aria-modal=\"true\"",
                        "/js/gradebook-confirm.js");
        assertThat(javascript).contains("formulario.requestSubmit(boton)",
                "evento.key === 'Escape'", "evento.key === 'Tab'",
                "¿Publicar resultados?", "¿Reabrir este bloque?");
        assertThat(estilos).contains(".gradebook-confirm-overlay",
                ".gradebook-confirm-dialog", "body.gradebook-modal-open",
                "[data-theme=dark] .gradebook-confirm-dialog");
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
