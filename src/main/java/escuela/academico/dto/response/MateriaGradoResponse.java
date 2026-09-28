package escuela.academico.dto.response;

import escuela.academico.entity.TipoEvaluacion;
import escuela.common.dto.response.AuditoriaResponse;

import java.math.BigDecimal;

public record MateriaGradoResponse(
        Long id, Long gradoId, String grado, String nivel, TipoEvaluacion tipoEvaluacion,
        BigDecimal escalaMinima, BigDecimal escalaMaxima, BigDecimal minimaAprobatoria,
        int decimales, int orden, BigDecimal horasSemanales, boolean incluirBoleta,
        boolean activo, AuditoriaResponse auditoria
) {}

