package escuela.admin.service;

import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DevolucionSeleccionInterfazTest {
    @Test void conservaCheckboxRealesObligatoriedadYContratoDelCalculo() throws Exception {
        String html=Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        assertThat(html).contains("class=\"refund-benefit-consent\"","th:field=\"*{revisionAcuerdoAnticipado}\" required",
            "th:field=\"*{aplicacionIdsRevertir}\"","data-amount=${a.monto}","data-charge=${a.cargoId}",
            "Revisé las condiciones y autorizo este tratamiento del beneficio.","Marca esta autorización para continuar");
        String css=Files.readString(Path.of("src/main/resources/static/css/forms.css"));
        assertThat(css).contains(".refund-applications > label:has(input:checked)",".refund-benefit-consent:has(input:checked)",
            "content:'Seleccionado'",".refund-benefit-consent input[type=checkbox]:focus-visible",
            "@media(forced-colors:active)",".refund-applications > label b { grid-column:2");
    }
}
