package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class TutorAccesoInterfazTest {
    @Test
    void portalFamiliarDelTutorUsaPanelResponsivoConTemaOscuro() throws Exception {
        assertThat(recurso("templates/admin/tutor-form.html"))
                .contains("/css/tutor-access.css", "portal-access-card tutor-access-card",
                        "tutor-access-icon", "tutor-access-button primary", "tutor-access-button danger");
        assertThat(recurso("static/css/tutor-access.css"))
                .contains(".tutor-access-card", ".portal-account-details", ".tutor-access-button.primary",
                        "[data-theme=dark] .tutor-access-card", "@media (max-width: 720px)");
    }

    private String recurso(String ruta) throws Exception {
        try (var entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
