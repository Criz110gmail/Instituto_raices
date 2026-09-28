package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class BoletaInterfazTest {
    @Test
    void conservaFiltrosEnExportacionesYAbrePdfSinDescargaForzada() throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream("templates/admin/boletas.html")) {
            assertThat(entrada).isNotNull();
            String html = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            assertThat(html).contains("/admin/boletas/excel", "/admin/boletas/pdf",
                    "plantelId=${filtro.plantelId}", "grupoId=${filtro.grupoId}", "q=${filtro.q}",
                    "target=\"_blank\"", "data-endpoint=\"/admin/autocompletado/grupos-boleta\"");
        }
    }
}
