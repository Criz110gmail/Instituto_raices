package escuela.calificacion.dto;

import escuela.academico.entity.TipoEvaluacion;

import java.math.BigDecimal;
import java.time.Instant;

public record PortalCalificacionResponse(
        String ciclo,
        String periodo,
        String materia,
        TipoEvaluacion tipoEvaluacion,
        BigDecimal valorNumerico,
        String valorCualitativo,
        BigDecimal minimaAprobatoria,
        int decimales,
        String observaciones,
        Instant publicadoEn
) {}
