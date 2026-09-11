package escuela.institucion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PlantelRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 200) String nombre,
        @Size(max = 30) String telefono,
        @Email @Size(max = 254) String email,
        @Size(max = 200) String calle,
        @Size(max = 30) String numeroExterior,
        @Size(max = 30) String numeroInterior,
        @Size(max = 150) String colonia,
        @Size(max = 120) String ciudad,
        @Size(max = 120) String estado,
        @Size(max = 12) String codigoPostal,
        @Pattern(regexp = "[A-Z]{2}") String pais,
        boolean activo,
        Long version
) {
}
