package escuela.admin.controller;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import static org.assertj.core.api.Assertions.assertThat;

class ReportesNavegacionRenderTest {
    @Test void cadaReporteConservaLosCuatroDestinosYUnSoloDestinoActual() {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        var engine = new SpringTemplateEngine();
        engine.setTemplateResolver(resolver);
        for (String active : Set.of("tesoreria", "cuenta", "alumno", "cobranza")) {
            var context = new Context();
            context.setVariable("active", active);
            String html = engine.process("admin/fragments/reportes-navegacion", context);
            assertThat(html).contains("Selecciona un reporte",
                    "href=\"/admin/reportes-financieros/tesoreria\"",
                    "href=\"/admin/reportes-financieros/estado-cuenta-cuenta\"",
                    "href=\"/admin/reportes-financieros/estado-cuenta\"",
                    "href=\"/admin/reportes-financieros/concentrado-cobranza\"");
            assertThat(html.split("aria-current=\"page\"", -1)).hasSize(2);
        }
    }
}
