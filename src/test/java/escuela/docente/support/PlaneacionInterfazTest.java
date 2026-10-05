package escuela.docente.support;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class PlaneacionInterfazTest {
    @Test void conservaCsrfFiltrosExcelPdfYAccesoResponsivo() throws Exception {
        assertThat(recurso("templates/login-maestros.html"))
                .contains("/css/login.css", "/css/login-portals.css", "/css/maestros-login.css",
                        "teacher-login-page", "Nexo Docente", "th:action=\"@{/login}\"",
                        "name=\"origen\" value=\"maestros\"");
        assertThat(recurso("templates/admin/maestro-form.html"))
                .contains("class=\"entity-form\"", "<fieldset class=\"form-section\"><legend>Identidad institucional</legend>",
                        "/css/maestro-form.css", "teacher-management-card teacher-access-card",
                        "teacher-management-card teacher-assignment-card", "teacher-account-actions",
                        "teacher-assignment-list", "teacher-empty-assignment");
        assertThat(recurso("static/css/maestro-form.css"))
                .contains(".teacher-card-heading", ".teacher-card-content", ".teacher-account-state.state-active",
                        "[data-theme=dark] .teacher-management-card", "@media (max-width: 700px)");
        assertThat(recurso("templates/maestros/inicio.html"))
                .contains("class=\"teacher-plan-filters\"", "name=\"desde\"", "name=\"hasta\"",
                        "name=\"estado\"", "class=\"teacher-plan-table\"", "Aplicar filtros",
                        "pagina=${planeaciones.number-1}", "pagina=${planeaciones.number+1}");
        assertThat(recurso("static/css/maestros-login.css"))
                .contains(".teacher-login-page", ".teacher-login-story", ".teacher-constellation",
                        "[data-theme=dark] .teacher-login-page");
        assertThat(recurso("static/css/maestro-portal.css"))
                .contains(".teacher-plan-filters", ".teacher-plan-table", ".teacher-plan-action",
                        ".teacher-plan-table td::before");
        assertThat(recurso("templates/maestros/planeacion-form.html")).contains("th:action=","data-plan-subjects","data-add-activity","actividades[");
        assertThat(recurso("templates/admin/planeaciones.html")).contains("/admin/planeaciones/excel","estado=${filtro.estado}","desde=${filtro.desde}","hasta=${filtro.hasta}", "class=\"filter-actions\"",
                "/admin/autocompletado/maestros-planeacion", "/admin/autocompletado/grupos-planeacion",
                "name=\"proposito\"", "name=\"maestroId\"", "name=\"grupoId\"", "/js/autocomplete.js",
                "class=\"table-action planning-review-button\"");
        assertThat(recurso("static/css/forms.css")).contains(".planning-filters .filter-actions", ".planning-filters .filter-actions button");
        assertThat(recurso("templates/admin/planeacion-detalle.html")).contains("target=\"_blank\"","/publicar","/reabrir","name=\"motivo\"", "class=\"admin-back\"");
        assertThat(recurso("static/css/maestros.css")).contains(".planning-review-button", ".admin-back{align-items:center");
    }
    private String recurso(String ruta)throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream(ruta)){assertThat(in).isNotNull();return new String(in.readAllBytes(),StandardCharsets.UTF_8);}}
}
