package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OrdenAutomaticoInterfazTest {

    @Test
    void losFormulariosNoSolicitanElOrdenAlUsuario() throws Exception {
        for (String plantilla : List.of(
                "nivel-form.html",
                "grado-form.html",
                "periodo-form.html",
                "materia-plan-form.html")) {
            String html = Files.readString(Path.of(
                    "src/main/resources/templates/admin", plantilla));

            assertThat(html)
                    .as("campo de orden visible en %s", plantilla)
                    .doesNotContain("*{orden}")
                    .doesNotContain("name=\"orden\"");
        }
    }

    @Test
    void laMigracionAlineaExistentesYAutomatizaAltas() throws Exception {
        String sql = Files.readString(Path.of(
                "src/main/resources/db/migration/V46__orden_automatico_por_id.sql"));

        assertThat(sql)
                .contains("UPDATE nivel_educativo SET orden = id::INTEGER")
                .contains("UPDATE grado SET orden = id::INTEGER")
                .contains("UPDATE periodo_academico SET orden = id::INTEGER")
                .contains("UPDATE materia_grado SET orden = id::INTEGER")
                .contains("BEFORE INSERT ON nivel_educativo")
                .contains("BEFORE INSERT ON grado")
                .contains("BEFORE INSERT ON periodo_academico")
                .contains("BEFORE INSERT ON materia_grado");
    }
}
