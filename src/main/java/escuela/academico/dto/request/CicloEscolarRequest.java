package escuela.academico.dto.request;

import escuela.academico.entity.EstadoAcademico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CicloEscolarRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotNull EstadoAcademico estado,
        boolean predeterminado,
        Long version
) {
}
