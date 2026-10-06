package escuela.admin.support;
import escuela.cobranza.dto.response.*;
import escuela.common.support.FormatoMoneda;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import java.nio.file.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
class SeleccionGeneracionRenderTest {
    @Test void filasSeRenderizanMarcadasConClaveYMontoPorPeriodoOCargo() throws Exception {
        var fecha=LocalDate.of(2026,10,5);
        var cargo=new VistaPreviaCargoAutomaticoFila(13L,"A-1","Alumno prueba","Centro","Prueba","Única","Octubre",fecha,
                new BigDecimal("1000"),"MXN",new BigDecimal("200"),"Beca 20%",new BigDecimal("800"),"AUTO:1:13:UNICA");
        assertThat(render("cargo-generar",cargo)).contains("data-selection-row checked","value=\"AUTO:1:13:UNICA\"","data-selection-amount=\"800\"");
        var recargo=new VistaPreviaRecargoFila(19L,"A-1","Alumno prueba","Centro","Prueba",fecha,1,"10%",1,
                new BigDecimal("800"),new BigDecimal("50"),new BigDecimal("850"));
        assertThat(render("recargo-generar",recargo)).contains("data-selection-row checked","value=\"RECARGO:19\"","data-selection-amount=\"50\"");
    }
    String render(String nombre,Object fila) throws Exception {
        var html=Files.readString(Path.of("src/main/resources/templates/admin/"+nombre+".html"));
        int inicio=html.indexOf("<tr th:each=\"fila:${vistaPrevia.pagina.content}\"");
        html=html.substring(inicio,html.indexOf("</tr>",inicio)+5).replace("th:each=\"fila:${vistaPrevia.pagina.content}\"","");
        var contexto=new Context();contexto.setVariable("fila",fila);
        var app=new StaticApplicationContext();app.getBeanFactory().registerSingleton("formatoMoneda",new FormatoMoneda());
        contexto.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,new ThymeleafEvaluationContext(app,null));
        var motor=new SpringTemplateEngine();motor.setTemplateResolver(new StringTemplateResolver());return motor.process(html,contexto);
    }
    @Test void ambosFormulariosConfirmanLaSeleccionYConservanTokenEnPaginas() throws Exception {
        for(var nombre:List.of("cargo-generar","recargo-generar")) {
            var html=Files.readString(Path.of("src/main/resources/templates/admin/"+nombre+".html"));
            assertThat(html).contains("data-selection-confirm","name=\"seleccionId\"","seleccionId=${vistaPrevia.seleccionId}",
                    "data-selection-none","data-selection-all","generation-selection.js","data-selection-summary","type=\"submit\" disabled");
        }
    }
}
