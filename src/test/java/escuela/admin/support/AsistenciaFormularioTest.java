package escuela.admin.support;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class AsistenciaFormularioTest {
    @Test void capturaUsaAccionThymeleafParaIncluirCsrf() throws IOException {
        try(var entrada=getClass().getClassLoader().getResourceAsStream("templates/admin/asistencia-captura.html")){
            assertThat(entrada).isNotNull();
            assertThat(new String(entrada.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("method=\"post\" th:action=\"@{/admin/asistencia/captura}\"")
                    .contains("academic-capture-page", "academic-capture-card",
                            "academic-capture-intro", "capture-selector-section",
                            "capture-workspace", "attendance-summary", "gradebook-table-wrap",
                            "data-label=\"Alumno\"", "data-label=\"Estado\"");
        }
    }
}
