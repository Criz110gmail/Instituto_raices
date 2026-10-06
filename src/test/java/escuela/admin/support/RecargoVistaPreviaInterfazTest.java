package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class RecargoVistaPreviaInterfazTest {

    @Test
    void exigeVistaPreviaLegibleAntesDeConfirmar() throws Exception {
        String html = Files.readString(Path.of(
                "src/main/resources/templates/admin/recargo-generar.html"));

        assertThat(html).contains("Visualizar recargos",
                "/admin/politicas-recargo/generar/vista-previa",
                "Recargos que se aplicarán", "Saldo actual", "Nuevo saldo",
                "Confirmar y generar recargos", "vistaPrevia.pagina.content",
                "errorOperacion != null or #fields.hasErrors('*')",
                "generation-confirm-button", "data-preview-form", "data-generation-preview",
                "/js/generation-preview.js");
        assertThat(html).doesNotContain("${errorOperacion or #fields.hasErrors('*')}");
        assertThat(html)
                .contains("th:action=\"@{/admin/politicas-recargo/generar/vista-previa}\"")
                .contains("method=\"post\"", "th:action=\"@{/admin/politicas-recargo/generar}\"", "data-selection-confirm", "name=\"seleccionId\"");
        assertThat(html.indexOf("Visualizar recargos"))
                .isLessThan(html.indexOf("Confirmar y generar recargos"));
    }
}
