package escuela.inscripcion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AsignacionGrupoRequest(
        @NotNull Long grupoId,
        @NotNull LocalDate fechaInicio,
        @Size(max = 2000) String motivo
) {
}
