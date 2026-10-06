package escuela.portal.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PortalPagoInterfazTest {

    @Test
    void transferenciaSigueElOrdenDeCapturaYRevisaSinVolverArriba() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/portal/pago-form.html"));
        String css = Files.readString(Path.of("src/main/resources/static/css/portal-pago.css"));
        String js = Files.readString(Path.of("src/main/resources/static/js/portal-pago.js"));
        assertThat(html.indexOf("id=\"total-transferencia\""))
                .isLessThan(html.indexOf("1 · Selecciona qué vas a pagar"));
        assertThat(html.indexOf("1 · Selecciona qué vas a pagar"))
                .isLessThan(html.indexOf("2 · Selecciona la cuenta destino"));
        assertThat(html.indexOf("2 · Selecciona la cuenta destino"))
                .isLessThan(html.indexOf("3 · Datos de la transferencia"));
        assertThat(html.indexOf("3 · Datos de la transferencia"))
                .isLessThan(html.indexOf("4 · Adjunta el comprobante"));
        assertThat(html.indexOf("4 · Adjunta el comprobante"))
                .isLessThan(html.indexOf("5 · Revisa y envía"));
        assertThat(html).contains("resumen-cargos", "resumen-total", "resumen-cuenta",
                "resumen-fecha", "resumen-referencia", "resumen-archivos", "archivos-seleccionados",
                "required disabled aria-describedby=\"cuenta-ayuda\"", "Enviar transferencia a revisión")
                .contains("th:action=\"@{/portal/pagos/reportar}\"", "enctype=\"multipart/form-data\"");
        assertThat(css).contains("position:sticky;top:82px", "overflow:visible", "var(--card)",
                "@media(max-width:680px)", "scroll-margin-top:190px");
        assertThat(js).contains("cuenta.disabled = true", "cuenta.disabled = !datos.resultados.length",
                "form?.addEventListener('input', actualizarResumen)", "item.textContent = valor",
                "listaArchivos.replaceChildren()");
    }

    @Test
    void plantelEImportesSonCalculadosYLosCargosSeAbrenConFocoOClic() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/portal/pago-form.html"));
        String javascript = Files.readString(Path.of("src/main/resources/static/js/portal-pago.js"));

        assertThat(html)
                .contains("Plantel asignado automáticamente", "Total a transferir", "Saldo a pagar",
                        "readonly aria-readonly=\"true\"", "portal-charge-amount-display",
                        "data-cargo-autocomplete", "/js/portal-pago.js")
                .doesNotContain("Plantel que recibe el pago", "th:field=\"*{plantelRegistroId}\"")
                .doesNotContain("cargos-list", "<datalist");
        assertThat(javascript)
                .contains("entrada.addEventListener('focus', mostrarIniciales)")
                .contains("entrada.addEventListener('click'")
                .contains("/cuentas?cargoId=")
                .contains("importe.value = Number(opcion.monto).toFixed(2)")
                .contains("montoTotal.value = total > 0 ? total.toFixed(2) : ''")
                .contains("formatoMoneda.format(total)");
    }

    @Test
    void historialFamiliarUsaTablaFiltrosYComprobantesEnOtraPestana() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));

        assertThat(html).contains("family-payment-table", "name=\"mes\"", "name=\"anio\"",
                        "Historial por alumno", "Ver comprobante PDF", "target=\"_blank\"")
                .contains("portal.hijo.nombre", "paginaPagos");
        assertThat(html).contains("Resumen de tu cuenta", "Pendiente por pagar",
                "Lo que falta por pagar", "Tus pagos y comprobantes", "Falta pagar",
                "Importe del pago para este alumno", "Las transferencias en revisión",
                "¡Estás al corriente!", "Con abonos", "De este saldo,")
                .doesNotContain("<th>Aplicado al alumno</th>");
    }
}
