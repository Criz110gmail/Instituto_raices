package escuela.seguridad.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RolRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 2000) String descripcion,
        boolean activo,
        Long version
) {
}
