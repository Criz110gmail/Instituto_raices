package escuela.calificacion.dto;

import escuela.calificacion.entity.EstadoCalificacion;

import java.math.BigDecimal;

public record FilaCalificacionResponse(
        Long inscripcionId,
        Long calificacionId,
        String matricula,
        String alumno,
        BigDecimal valorNumerico,
        String valorCualitativo,
        String observaciones,
        EstadoCalificacion estado,
        Long version
) {}
