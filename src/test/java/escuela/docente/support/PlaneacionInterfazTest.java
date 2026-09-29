package escuela.docente.support;

import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import static org.assertj.core.api.Assertions.assertThat;

class PlaneacionInterfazTest {
    @Test void conservaCsrfFiltrosExcelPdfYAccesoResponsivo() throws Exception {
        assertThat(recurso("templates/login-maestros.html"))
                .contains("/css/login.css", "/css/login-portals.css", "th:action=\"@{/login}\"", "name=\"origen\" value=\"maestros\"");
        assertThat(recurso("templates/admin/maestro-form.html"))
                .contains("class=\"entity-form\"", "<fieldset class=\"form-section\"><legend>Identidad institucional</legend>",
                        "/css/maestro-form.css");
        assertThat(recurso("templates/maestros/planeacion-form.html")).contains("th:action=","data-plan-subjects","data-add-activity","actividades[");
        assertThat(recurso("templates/admin/planeaciones.html")).contains("/admin/planeaciones/excel","estado=${filtro.estado}","desde=${filtro.desde}","hasta=${filtro.hasta}");
        assertThat(recurso("templates/admin/planeacion-detalle.html")).contains("target=\"_blank\"","/publicar","/reabrir","name=\"motivo\"");
    }
    private String recurso(String ruta)throws Exception{try(var in=getClass().getClassLoader().getResourceAsStream(ruta)){assertThat(in).isNotNull();return new String(in.readAllBytes(),StandardCharsets.UTF_8);}}
}
