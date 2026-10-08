package escuela.admin.support;

import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class PagoCapturaOrdenTest {
    @Test void saldoPendienteDestacadoConTemaCompartidoYDistribucionMovil() throws Exception {
        var css=Files.readString(Path.of("src/main/resources/static/css/forms.css"));
        assertThat(css).contains(".payment-charge-review-balance", "font-size: clamp(1.8rem,4vw,2.4rem)",
                "border: 2px solid var(--blue)", "color: var(--ink)",
                "@media (max-width: 700px)");
    }
    @Test void capturaEnOrdenConDatosPagadorDespuesDeAdeudosYRevisionAntesDeConfirmar() throws Exception {
        var html=Files.readString(Path.of("src/main/resources/templates/admin/pago-form.html"));
        int anterior=-1;
        for(String titulo:new String[]{"1 · Origen y responsable","2 · Adeudos que se van a pagar","3 · Datos del pago","4 · Comprobantes y observaciones","5 · Revisa y registra"}) {
            int posicion=html.indexOf("<legend>"+titulo+"</legend>");
            assertThat(posicion).isGreaterThan(anterior);anterior=posicion;
        }
        assertThat(html.indexOf("*{nombrePagador}")).isGreaterThan(html.indexOf("3 · Datos del pago"));
        assertThat(html.indexOf("*{observaciones}")).isGreaterThan(html.indexOf("4 · Comprobantes y observaciones"));
        assertThat(html.indexOf("id=\"total-solicitado\"")).isBetween(html.indexOf("2 · Adeudos"),html.indexOf("3 · Datos"));
        assertThat(html).contains("id=\"allow-unassigned\"", "data-money-confirm=\"registro-pago\"",
                "th:action=\"@{/admin/pagos}\"", "enctype=\"multipart/form-data\"",
                "Registrar como pendiente abrirá un modal", "Cancelar cierra sin guardar",
                "id=\"payment-review-charges\"", "id=\"payment-review-account\"");
    }
}
