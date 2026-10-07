package escuela.admin.service;

import escuela.admin.dto.DevolucionPagoForm;
import org.junit.jupiter.api.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.validation.DataBinder;
import java.nio.file.*;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class CapturaMonetariaInterfazTest {
    @Test void todosLosImportesUsanComponenteCompartidoSinCambiarOtrosNumeros() throws Exception {
        try (var archivos = Files.list(Path.of("src/main/resources/templates/admin"))) {
            for (Path path : archivos.filter(p -> p.toString().endsWith(".html")).toList()) {
                String html = Files.readString(path);
                var tags = java.util.regex.Pattern.compile("<input[^>]*>").matcher(html);
                while (tags.find()) {
                    String tag = tags.group();
                    if (tag.contains("type=\"number\"") && tag.matches(".*(monto|saldoInicial|efectivoDeclarado|precioUnitario|importeBase|importeOriginal|valorLimite).*"))
                        fail("Importe con number en " + path + ": " + tag);
                    if (tag.contains("data-money")) {
                        assertThat(tag).contains("type=\"text\"", "inputmode=\"decimal\"");
                        assertThat(html).contains("/js/money-input.js", "/css/money-input.css");
                    }
                }
            }
        }
        String grupo = Files.readString(Path.of("src/main/resources/templates/admin/grupo-form.html"));
        assertThat(grupo).contains("type=\"number\" min=\"1\"");
        String politica = Files.readString(Path.of("src/main/resources/templates/admin/politica-recargo-form.html"));
        assertThat(politica).contains("data-money-scale=\"4\"", "data-money-when=\"tipoLimite:MONTO_FIJO\"");
    }
    @Test void montoCanonicoMantieneBindingDecimalDelServidor() {
        var form = new DevolucionPagoForm(); var binder = new DataBinder(form);
        binder.bind(new MutablePropertyValues(java.util.Map.of("monto", "1400.50")));
        assertThat(binder.getBindingResult().hasErrors()).isFalse();
        assertThat(form.getMonto()).isEqualByComparingTo(new BigDecimal("1400.50"));
    }
    @Test void confirmacionesSonEspecificasYNoAlertDeJavascript() throws Exception {
        String pago = Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        String cargo = Files.readString(Path.of("src/main/resources/templates/admin/cargo-detalle.html"));
        assertThat(pago).contains("data-money-confirm=\"devolucion\"", "data-money-confirm=\"validacion\"");
        assertThat(cargo).contains("data-money-confirm=\"ajuste\"");
        String js = Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
        assertThat(js).contains("dialog.showModal()", "form.requestSubmit", "textContent", "WeakSet", "type = 'hidden'")
                .doesNotContain("innerHTML", "window.confirm", "alert(");
    }
    @Test void migracionSoloActualizaContenidoEditorialParaNuevosBotones() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V66__guias_confirmacion_monetaria.sql"));
        assertThat(sql).contains("UPDATE guia_proceso_paso", "UPDATE guia_proceso", "Confirmar operación", "version_contenido + 1")
                .doesNotContain("UPDATE pago", "UPDATE cargo", "INSERT INTO rol_permiso", "ALTER TABLE");
    }
    @Test void focoPorClickSoloEnCampoSinReutilizarClaseDeContenedorExistente() throws Exception {
        String js = Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
        assertThat(js).contains("visible.className = 'money-entry'", "visible.closest('label')",
                "event.target === visible", "event.detail === 0", "button,a,input,select,textarea")
                .doesNotContain("visible.className = 'money-input'");
        String css = Files.readString(Path.of("src/main/resources/static/css/money-input.css"));
        assertThat(css).contains(".money-entry").doesNotContain(".money-input {");
    }
}
