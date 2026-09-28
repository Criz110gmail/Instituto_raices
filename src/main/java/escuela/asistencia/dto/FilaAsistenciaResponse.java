package escuela.asistencia.dto;

import escuela.asistencia.entity.EstadoAsistencia;

public record FilaAsistenciaResponse(Long inscripcionId, Long asistenciaId, String matricula,
                                     String alumno, EstadoAsistencia estado,
                                     String observaciones, Long version) {}
