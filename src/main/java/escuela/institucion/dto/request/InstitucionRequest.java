package escuela.institucion.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record InstitucionRequest(
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 200) String nombre,
        @Size(max = 200) String nombreComercial,
        @Size(max = 250) String razonSocial,
        @Size(max = 13) String rfc,
        @Email @Size(max = 254) String email,
        @Size(max = 30) String telefono,
        @Size(max = 300) String sitioWeb,
        String domicilioFiscal,
        @Size(max = 120) String ciudad,
        @Size(max = 120) String estado,
        @Size(max = 12) String codigoPostal,
        @Pattern(regexp = "[A-Z]{2}") String pais,
        @NotBlank @Size(max = 60) String zonaHoraria,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String monedaPredeterminada,
        boolean activo,
        Long version
) {
}
