package escuela.academico.dto.request;

import escuela.academico.entity.Turno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record GrupoRequest(
        @NotNull Long plantelId,
        @NotNull Long cicloEscolarId,
        @NotNull Long gradoId,
        @NotBlank @Size(max = 100) String nombre,
        @NotNull Turno turno,
        @Size(max = 50) String codigo,
        @Size(max = 100) String aula,
        @Positive Integer capacidad,
        boolean activo,
        Long version
) {
}
