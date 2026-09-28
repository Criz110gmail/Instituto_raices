package escuela.calificacion.dto;

import escuela.academico.entity.TipoEvaluacion;

import java.math.BigDecimal;
import java.util.List;

public record HojaCalificacionesResponse(
        Long grupoId,
        String grupo,
        String plantel,
        String ciclo,
        String grado,
        Long periodoId,
        String periodo,
        Long materiaGradoId,
        String materia,
        TipoEvaluacion tipoEvaluacion,
        BigDecimal escalaMinima,
        BigDecimal escalaMaxima,
        BigDecimal minimaAprobatoria,
        int decimales,
        boolean publicada,
        List<FilaCalificacionResponse> filas
) {}
