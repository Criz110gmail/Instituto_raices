package escuela.common.support;

import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticApplicationContext;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class FormatoMonedaTest {

    private final FormatoMoneda formato = new FormatoMoneda();

    @Test
    void conservaDosDecimalesYAgregaSeparadoresDeMiles() {
        assertThat(formato.pesos(new BigDecimal("150.52"))).isEqualTo("$150.52");
        assertThat(formato.pesos(new BigDecimal("1000000.5"))).isEqualTo("$1,000,000.50");
    }

    @Test
    void representaNegativosYCeroComoImportes() {
        assertThat(formato.pesos(new BigDecimal("-1250.7"))).isEqualTo("-$1,250.70");
        assertThat(formato.pesos(BigDecimal.ZERO)).isEqualTo("$0.00");
    }

    @Test
    void puedeUsarseComoBeanDesdeLasPlantillasThymeleaf() {
        var aplicacion = new StaticApplicationContext();
        aplicacion.getBeanFactory().registerSingleton("formatoMoneda", formato);
        var motor = new SpringTemplateEngine();
        motor.setTemplateResolver(new StringTemplateResolver());
        var contexto = new Context();
        contexto.setVariable("monto", new BigDecimal("1000000.5"));
        contexto.setVariable(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                new ThymeleafEvaluationContext(aplicacion, null));

        assertThat(motor.process("<b th:text=\"${@formatoMoneda.pesos(monto)}\"></b>", contexto))
                .isEqualTo("<b>$1,000,000.50</b>");
    }
}
