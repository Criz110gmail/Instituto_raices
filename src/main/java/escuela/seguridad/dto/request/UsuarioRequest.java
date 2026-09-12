package escuela.seguridad.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Email @Size(max = 254) String email,
        Long version
) {
}
