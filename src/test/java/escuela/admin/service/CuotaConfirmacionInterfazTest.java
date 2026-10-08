package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;

class CuotaConfirmacionInterfazTest {
    @Test void altaReutilizaModalSinAlterarPostNiEdicion() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/cuota-alumno-form.html"));
        assertThat(html).contains("data-money-confirm=${edicion ? null : 'cuota'}", "th:object=\"${form}\"",
                "method=\"post\"", "th:action=", "/js/money-input.js", "/css/money-input.css");
        String js = Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
        assertThat(js).contains("Confirmar nueva cuota", "Confirmar y crear cuota", "['cuota', 'registro-pago', 'cancelacion-cargo', 'convenio', 'generacion-cargos', 'transferencia-familiar'].includes(kind) || paymentDecision ? 'Cancelar'",
                "Alumno e inscripción", "Importe por mes", "Meses a cobrar", "Crear cargo al confirmar",
                "form.reportValidity()", "form.requestSubmit(button || undefined)");
        assertThat(js).doesNotContain("innerHTML", "form.submit()");
    }
    @Test void guiaActualizaSoloTextoDeCuotasYVersiones() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V69__guias_confirmacion_cuota.sql"));
        assertThat(sql).contains("UPDATE guia_proceso_paso", "UPDATE guia_proceso", "modulo='Cuotas por alumno'",
                "Cancelar cierra sin guardar", "Confirmar y crear cuota", "version_contenido=version_contenido+1");
        assertThat(sql).doesNotContain("INSERT INTO", "UPDATE cuota_alumno", "UPDATE cargo", "ALTER TABLE", "rol_permiso");
    }
}
