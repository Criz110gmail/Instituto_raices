package escuela.docente.dto;

import escuela.docente.entity.EstadoPlaneacion;
import java.time.LocalDate;

public record PlaneacionFila(Long id, String maestro, String numeroEmpleado, String plantel,
                             String grupo, String grado, LocalDate fechaInicio, LocalDate fechaFin,
                             EstadoPlaneacion estado, int revision, long materias, Long version) { }
