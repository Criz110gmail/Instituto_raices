package escuela.admin.support;
import org.junit.jupiter.api.Test;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class CuotasExcluidasVisibilidadTest {
    @Test void diagnosticoCerradoPorDefectoYAbiertoAlPaginarExplicitamente() throws Exception {
        String html=Files.readString(Path.of("src/main/resources/templates/admin/cargo-generar.html"));
        int inicio=html.indexOf("<details id=\"cuotas-no-incluidas\"");
        String fragmento=html.substring(inicio,html.indexOf("<section class=\"surcharge-preview-card\">",inicio))+"</details>";
        var contexto=new Context();contexto.setVariable("cuotasExcluidas",new Object());
        var motor=new SpringTemplateEngine();motor.setTemplateResolver(new StringTemplateResolver());
        contexto.setVariable("mostrarExcluidas",false);
        assertThat(motor.process(fragmento,contexto)).contains("Consultar cuotas no incluidas","no se generarán cargos").doesNotContain("open=");
        contexto.setVariable("mostrarExcluidas",true);
        assertThat(motor.process(fragmento,contexto)).contains("open=\"open\"");
        assertThat(html).contains("mostrarExcluidas=true","Exportar motivos a Excel");
    }
}
