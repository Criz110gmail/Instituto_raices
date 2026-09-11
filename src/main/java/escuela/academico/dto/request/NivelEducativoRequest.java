package escuela.academico.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record NivelEducativoRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        String descripcion,
        @Positive int orden,
        boolean activo,
        Long version
) {
}
