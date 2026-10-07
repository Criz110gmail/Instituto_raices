package escuela.portal.support;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class PortalTransferenciaMensajesTest {
    @Test void guiaConservaEnlacesAdministrativosONulosSinInsertarPagos() throws Exception {
        var sql=Files.readString(Path.of("src/main/resources/db/migration/V62__guia_transferencia_vencida.sql"));
        assertThat(sql).contains("DATE '2026-10-07'", "'CONFIRMADA'", "NULL),")
                .doesNotContain("', '/familias')", "INSERT INTO pago", "INSERT INTO cargo", "UPDATE rol");
    }
    @Test void avisoRenderizaEstadoDeRevisionYEscapaElMensaje() throws Exception {
        var html=Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));
        int inicio=html.indexOf("<section class=\"family-transfer-success\"");
        var fragmento=html.substring(inicio,html.indexOf("</section>",inicio)+10);
        var engine=new SpringTemplateEngine();engine.setTemplateResolver(new StringTemplateResolver());
        var contexto=new Context();contexto.setVariable("mensajePortal","Transferencia enviada. <script>prueba</script>");
        var resultado=engine.process(fragmento,contexto);
        assertThat(resultado).contains("Comprobante recibido","En revisión","role=\"status\"","aria-live=\"polite\"","&lt;script&gt;","El saldo se actualizará").doesNotContain("<script>");
        contexto.setVariable("mensajePortal",null);assertThat(engine.process(fragmento,contexto)).doesNotContain("Comprobante recibido");
    }
    @Test void resumenEsSoloLecturaYDistingueColoresConLosTemasExistentes() throws Exception {
        var html=Files.readString(Path.of("src/main/resources/templates/portal/pago-form.html"));
        int inicio=html.indexOf("<fieldset class=\"portal-payment-section portal-transfer-review\"");
        var resumen=html.substring(inicio,html.indexOf("</fieldset>",inicio));
        assertThat(resumen).contains("Sólo revisión · no necesitas capturar nada aquí","se completa automáticamente","resumen-total").doesNotContain("<input","<select","<textarea");
        var css=Files.readString(Path.of("src/main/resources/static/css/portal-pago.css"));
        assertThat(css).contains("background:var(--lavender-soft)",".portal-review-heading","@media(max-width:680px)");
        var portal=Files.readString(Path.of("src/main/resources/static/css/portal.css"));
        assertThat(portal).contains(".family-transfer-success","var(--mint-soft)",".family-transfer-success-state{grid-column:2");
    }
}
