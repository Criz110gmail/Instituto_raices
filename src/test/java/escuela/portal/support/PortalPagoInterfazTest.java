package escuela.portal.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PortalPagoInterfazTest {

    @Test
    void losImportesSonCalculadosYLaAyudaSeAbreDesdeElFoco() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/portal/pago-form.html"));
        String javascript = Files.readString(Path.of("src/main/resources/static/js/portal-pago.js"));

        assertThat(html)
                .contains("Total a transferir", "Saldo a pagar", "readonly aria-readonly=\"true\"",
                        "data-cargo-autocomplete", "/js/portal-pago.js")
                .doesNotContain("cargos-list", "<datalist");
        assertThat(javascript)
                .contains("entrada.addEventListener('focus', () => buscar(''))")
                .contains("importe.value = Number(opcion.monto).toFixed(2)")
                .contains("montoTotal.value = total > 0 ? total.toFixed(2) : ''");
    }

    @Test
    void historialFamiliarUsaTablaFiltrosYComprobantesEnOtraPestana() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));

        assertThat(html).contains("family-payment-table", "name=\"mes\"", "name=\"anio\"",
                        "Historial por alumno", "Ver comprobante PDF", "target=\"_blank\"")
                .contains("portal.hijo.nombre", "paginaPagos");
    }
}
