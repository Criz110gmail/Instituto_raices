package escuela.academico.dto.request;

import escuela.academico.entity.TipoEvaluacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MateriaGradoRequest(
        @NotNull Long gradoId,
        @NotNull TipoEvaluacion tipoEvaluacion,
        BigDecimal escalaMinima,
        BigDecimal escalaMaxima,
        BigDecimal minimaAprobatoria,
        @Min(0) @Max(2) int decimales,
        @Positive int orden,
        @DecimalMin(value = "0", inclusive = false) BigDecimal horasSemanales,
        boolean incluirBoleta,
        Long version
) {}

