package escuela.common.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AyudaContextualInterfazTest {

    @Test
    void todasLasPlantillasConCamposCarganElComponenteGlobal() throws IOException {
        Path plantillas = Path.of("src/main/resources/templates");
        try (var archivos = Files.walk(plantillas)) {
            List<Path> sinAyuda = archivos
                    .filter(ruta -> ruta.toString().endsWith(".html"))
                    .filter(ruta -> !ruta.toString().contains("/fragments/"))
                    .filter(this::contieneCamposVisibles)
                    .filter(ruta -> !leer(ruta).contains("theme.js"))
                    .toList();
            assertThat(sinAyuda).as("plantillas con campos sin ayuda contextual").isEmpty();
        }
    }

    @Test
    void componenteIncluyeCamposFiltrosDinamicosModalYNavegacionDirectaEntreControles() throws IOException {
        String tema = leer(Path.of("src/main/resources/static/js/theme.js"));
        String ayuda = leer(Path.of("src/main/resources/static/js/contextual-help.js"));
        String estilos = leer(Path.of("src/main/resources/static/css/contextual-help.css"));

        assertThat(tema).contains("/js/contextual-help.js", "/css/contextual-help.css");
        assertThat(ayuda).contains("querySelectorAll('label')", "input,select,textarea",
                "MutationObserver", "aria-haspopup", "aria-modal", "Escape",
                "¿Qué hace este módulo?", "MODULE_FIELD", "fallback",
                "button.tabIndex = -1", "button, control",
                "previousFocus?.isConnected");
        assertThat(ayuda.split("button\\.tabIndex = -1", -1)).hasSize(3);
        assertThat(estilos).contains(".context-help-trigger", ".context-module-help",
                ".context-help-overlay", ".filters .context-help-trigger",
                "aspect-ratio:1/1", "border-radius:999px!important",
                "@media(max-width:650px)", "[data-theme=dark]",
                "ningún formulario puede deformar los controles de ayuda",
                "width: 22px !important", "background: var(--accent,#16a9c7) !important");
    }

    @Test
    void accesosYFiltrosCompartenGeometriaRedondeadaResponsiva() throws IOException {
        String tema = leer(Path.of("src/main/resources/static/js/theme.js"));
        String estilos = leer(Path.of("src/main/resources/static/css/ui-polish.css"));

        assertThat(tema).contains("/css/ui-polish.css", "data-ui-polish");
        assertThat(estilos).contains(".family-login", "border-radius: 28px",
                ".filters", ".ledger-filters", ".event-filters",
                "border-radius: 20px", ".family-access form .context-help-trigger",
                "[data-theme=\"dark\"]", "@media (max-width: 480px)");
    }

    @Test
    void politicaRecargoDistingueLaAyudaDeLosMensajesDeError() throws IOException {
        String formulario = leer(Path.of("src/main/resources/templates/admin/politica-recargo-form.html"));
        String estilos = leer(Path.of("src/main/resources/static/css/forms.css"));

        assertThat(formulario).contains(
                "<small class=\"field-help\">El cargo empieza a generar después del vencimiento más estos días.</small>");
        assertThat(estilos).contains(".form-grid small.field-help { margin: 5px 0 0; color: var(--muted);");
    }

    @Test
    void autocompletadosConservanSuDisenoDentroDeFormulariosEspecializados() throws IOException {
        String estilos = leer(Path.of("src/main/resources/static/css/forms.css"));

        assertThat(estilos).contains(
                "Contrato visual global del autocompletado",
                ".autocomplete .autocomplete-clear",
                ".autocomplete .autocomplete-results",
                ".autocomplete .autocomplete-option",
                ".autocomplete .autocomplete-status",
                "overflow-x: hidden !important",
                "box-sizing: border-box !important",
                "background: transparent !important",
                "background: #f8fbfe !important",
                "color: var(--muted) !important",
                "[data-theme=dark] .autocomplete .autocomplete-results");
    }

    private boolean contieneCamposVisibles(Path ruta) {
        String html = leer(ruta);
        return html.matches("(?s).*(<input(?![^>]*type=\\\"hidden\\\")|<select|<textarea).*" );
    }

    private String leer(Path ruta) {
        try {
            return Files.readString(ruta);
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo leer " + ruta, ex);
        }
    }
}
