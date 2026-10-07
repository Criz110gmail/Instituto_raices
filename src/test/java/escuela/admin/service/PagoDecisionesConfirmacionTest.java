package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;

class PagoDecisionesConfirmacionTest {
    @Test void lasTresAccionesTienenContextoYConfirmacionPropia() throws Exception {
        String html=Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        assertThat(html).contains("data-money-confirm=\"validacion\"", "data-money-confirm=\"rechazo\"",
                "data-money-confirm=\"cancelacion\"", "data-confirm-folio=${pago.folio}",
                "data-confirm-state=${pago.estado.name()}", "data-confirm-account=${pago.cuentaDestinoNombre}");
        String js=Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
        assertThat(js).contains("Confirmar validación del pago", "Confirmar rechazo del pago", "Confirmar cancelación del pago",
                "Confirmar validación", "Confirmar rechazo", "Confirmar cancelación", "Motivo del rechazo",
                "Motivo de cancelación", "No cambiarán la cuenta ni los adeudos", "Egreso compensatorio",
                "form.requestSubmit(button || undefined)");
        assertThat(js).doesNotContain("innerHTML", "form.submit()");
    }
    @Test void actualizacionDeGuiasEsSoloEditorial() throws Exception {
        String sql=Files.readString(Path.of("src/main/resources/db/migration/V70__guias_confirmaciones_pago.sql"));
        assertThat(sql).contains("UPDATE guia_proceso_paso", "UPDATE guia_proceso", "Confirmar rechazo", "Confirmar validación");
        assertThat(sql).doesNotContain("UPDATE pago", "UPDATE cargo", "INSERT INTO", "ALTER TABLE", "rol_permiso");
    }
}
