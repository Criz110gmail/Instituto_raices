package escuela.calificacion.dto;

import java.math.BigDecimal;
import java.util.List;

public record CapturaCalificacionRequest(
        Long grupoId,
        Long periodoId,
        Long materiaGradoId,
        List<Fila> filas
) {
    public record Fila(Long inscripcionId, Long calificacionId, BigDecimal valorNumerico,
                       String valorCualitativo, String observaciones, Long version) {}
}
