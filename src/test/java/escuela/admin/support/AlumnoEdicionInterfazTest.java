package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AlumnoEdicionInterfazTest {

    @Test
    void organizaElExpedienteEnPestanasYAbreDocumentosEnOtraPestana() throws IOException {
        String plantilla = recurso("/templates/admin/alumno-form.html");

        assertThat(plantilla)
                .contains("data-student-tab=\"ficha\"")
                .contains("data-student-tab=\"informacion\"")
                .contains("data-student-tab=\"contacto\"")
                .contains("data-student-tab=\"notas\"")
                .contains("data-student-tab=\"documentos\"")
                .contains("data-student-tab=\"medica\"")
                .contains("data-student-panel=\"ficha\"")
                .contains("data-student-panel=\"informacion\"")
                .contains("data-student-panel=\"contacto\"")
                .contains("data-student-panel=\"notas\"")
                .contains("data-student-panel=\"documentos\"")
                .contains("data-student-panel=\"medica\"")
                .contains("/ficha.pdf", "/ficha-medica.pdf")
                .contains("target=\"_blank\"")
                .contains("student-edit-form")
                .contains("/visualizar")
                .contains("/js/student-tabs.js");
    }

    private String recurso(String ruta) throws IOException {
        try (var entrada = getClass().getResourceAsStream(ruta)) {
            assertThat(entrada).as("recurso %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
