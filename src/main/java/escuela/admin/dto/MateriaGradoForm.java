package escuela.admin.dto;

import escuela.academico.dto.request.MateriaGradoRequest;
import escuela.academico.dto.response.MateriaGradoResponse;
import escuela.academico.entity.TipoEvaluacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class MateriaGradoForm {
    @NotNull private Long gradoId;
    @Size(max = 300) private String gradoTexto;
    @NotNull private TipoEvaluacion tipoEvaluacion = TipoEvaluacion.NUMERICA;
    private BigDecimal escalaMinima = BigDecimal.ZERO;
    private BigDecimal escalaMaxima = BigDecimal.TEN;
    private BigDecimal minimaAprobatoria = new BigDecimal("6");
    @Min(0) @Max(2) private int decimales = 1;
    @DecimalMin(value = "0", inclusive = false) private BigDecimal horasSemanales;
    private boolean incluirBoleta = true;
    private Long version;

    public MateriaGradoRequest request() {
        return new MateriaGradoRequest(gradoId, tipoEvaluacion, escalaMinima,
                escalaMaxima, minimaAprobatoria, decimales, horasSemanales,
                incluirBoleta, version);
    }

    public static MateriaGradoForm desde(MateriaGradoResponse plan) {
        MateriaGradoForm form = new MateriaGradoForm();
        form.gradoId = plan.gradoId();
        form.gradoTexto = plan.nivel() + " · " + plan.grado();
        form.tipoEvaluacion = plan.tipoEvaluacion();
        form.escalaMinima = plan.escalaMinima();
        form.escalaMaxima = plan.escalaMaxima();
        form.minimaAprobatoria = plan.minimaAprobatoria();
        form.decimales = plan.decimales();
        form.horasSemanales = plan.horasSemanales();
        form.incluirBoleta = plan.incluirBoleta();
        form.version = plan.auditoria().version();
        return form;
    }
}
