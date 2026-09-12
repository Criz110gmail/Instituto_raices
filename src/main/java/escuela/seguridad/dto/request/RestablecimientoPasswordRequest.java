package escuela.seguridad.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestablecimientoPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 12, max = 72) String password
) {
}
