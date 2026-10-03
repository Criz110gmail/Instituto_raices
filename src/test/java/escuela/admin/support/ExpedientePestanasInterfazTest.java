package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ExpedientePestanasInterfazTest {

    @Test
    void maestroOrganizaFichaPortalYAsignacionesEnPestanas() throws Exception {
        assertThat(recurso("templates/admin/maestro-form.html"))
                .contains("/js/student-tabs.js", "data-student-tabs",
                        "data-student-tab=\"ficha\"", "data-student-tab=\"acceso\"",
                        "data-student-tab=\"asignaciones\"", "id=\"ficha-maestro\"",
                        "id=\"portal-maestro\"", "id=\"asignaciones\"");
    }

    @Test
    void tutorOrganizaExpedienteCompletoEnSeisPestanas() throws Exception {
        assertThat(recurso("templates/admin/tutor-form.html"))
                .contains("/js/student-tabs.js", "data-student-tabs",
                        "data-student-tab=\"ficha\"", "data-student-tab=\"identificacion\"",
                        "data-student-tab=\"informacion\"", "data-student-tab=\"contacto\"",
                        "data-student-tab=\"laboral\"", "data-student-tab=\"acceso\"",
                        "data-active-tab=${pestanaActiva == null ? 'identificacion' : pestanaActiva}",
                        "id=\"ficha-tutor\"", "id=\"informacion-tutor\"",
                        "id=\"contacto-tutor\"", "id=\"laboral-tutor\"",
                        "id=\"identificacion-tutor\"", "id=\"portal-familias\"",
                        "/ficha.pdf", "class=\"entity-form tutor-edit-form\"");
    }

    @Test
    void inscripcionOrganizaFichaYAsignacionesEnPestanas() throws Exception {
        assertThat(recurso("templates/admin/inscripcion-form.html"))
                .contains("/js/student-tabs.js", "data-student-tabs",
                        "data-student-tab=\"ficha\"", "data-student-tab=\"grupos\"",
                        "id=\"datos-inscripcion\"", "id=\"grupos-inscripcion\"",
                        "data-student-panel=\"ficha\"", "data-student-panel=\"grupos\"");
    }

    @Test
    void navegadorDePestanasAdmiteHashesConfigurables() throws Exception {
        assertThat(recurso("static/js/student-tabs.js"))
                .contains("dataset.tabHash", "data-open-student-tab", "history.replaceState");
    }

    private String recurso(String ruta) throws Exception {
        try (var entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
