package escuela.asistencia.dto;

import escuela.asistencia.entity.EstadoAsistencia;
import java.time.LocalDate;
import java.util.List;

public record CapturaAsistenciaRequest(Long grupoId, LocalDate fecha, List<Fila> filas) {
    public record Fila(Long inscripcionId, EstadoAsistencia estado, String observaciones, Long version) {}
}
