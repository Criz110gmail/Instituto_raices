package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;

class DevolucionCuentaInterfazTest {
    @Test void cuentaProtegidaMotivoYResumenUsanElDiseñoExistente() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        assertThat(html).contains("validation-account-protected", "th:readonly=\"${!devolucionForm.cambiarCuentaOrigen}\"",
                "Devolver desde otra cuenta", "refund-account-change-reason", "refund-account-summary",
                "th:field=\"*{motivoCambioCuenta}\"", "destination-change-reason");
        assertThat(html).contains("refund-account-toggle", "aria-pressed", "refund-change-account-value")
                .doesNotContain("type=\"checkbox\" id=\"refund-change-account\"");
        String css = Files.readString(Path.of("src/main/resources/static/css/forms.css"));
        assertThat(css).contains("#refund-account-change-reason[hidden] { display: none !important; }");
        assertThat(html).contains("refund-preview-heading", "Vista previa · No necesitas capturar aquí",
                "Todavía no se ha devuelto dinero");
        assertThat(css).contains("[data-theme=dark] .refund-preview", ".refund-preview-badge",
                ".refund-preview .distribution-total .remaining");
        String js = Files.readString(Path.of("src/main/resources/static/js/autocomplete.js"));
        assertThat(js).contains("if (entrada.readOnly) return", "autocomplete:restore", "actual.signal.aborted");
    }
}
