package escuela.docente.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AsignacionMaestroRequest(@NotNull Long grupoId, @NotNull Long materiaId,
                                       @NotNull LocalDate fechaInicio, LocalDate fechaFin,
                                       Long version) { }
