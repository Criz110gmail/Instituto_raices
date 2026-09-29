package escuela.docente.dto;

import java.time.LocalDate;

public record AsignacionMaestroResponse(Long id, Long grupoId, String grupo, String plantel,
                                        String ciclo, Long materiaId, String materia,
                                        LocalDate fechaInicio, LocalDate fechaFin,
                                        boolean activo, Long version) { }
