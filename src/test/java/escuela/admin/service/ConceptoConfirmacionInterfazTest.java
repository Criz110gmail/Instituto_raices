package escuela.admin.service;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class ConceptoConfirmacionInterfazTest {
    @Test void altaReutilizaModalComunSinAlterarEdicionNiEndpoint()throws Exception {
        String html=Files.readString(Path.of("src/main/resources/templates/admin/concepto-cobro-form.html"));
        assertThat(html).contains("data-money-confirm=${edicion ? null : 'concepto-cobro'}","/css/money-input.css","/js/money-input.js",
            "edicion ? '/admin/conceptos-cobro/' + id : '/admin/conceptos-cobro'");
    }
}
