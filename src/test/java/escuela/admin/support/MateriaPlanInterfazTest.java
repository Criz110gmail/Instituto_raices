package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class MateriaPlanInterfazTest {
    @Test
    void horasSemanalesAceptaHorasCompletasYDecimales() throws IOException {
        String html;
        try (var entrada = getClass().getClassLoader()
                .getResourceAsStream("templates/admin/materia-plan-form.html")) {
            assertThat(entrada).isNotNull();
            html = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }

        assertThat(html).contains("th:field=\"*{horasSemanales}\"");
        assertThat(html).contains("min=\"0.01\" step=\"0.01\"");
        assertThat(html).doesNotContain("min=\"0.01\" step=\"0.25\"");
    }
}
