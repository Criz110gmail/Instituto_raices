package escuela.tutor.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TutorRequest(
        @NotNull Long institucionId,
        Long usuarioId,
        @NotBlank @Size(max = 150) String nombres,
        @NotBlank @Size(max = 100) String primerApellido,
        @Size(max = 100) String segundoApellido,
        @NotBlank @Size(max = 30) String telefonoPrincipal,
        @Size(max = 30) String telefonoSecundario,
        @Email @Size(max = 254) String email,
        @PastOrPresent LocalDate fechaNacimiento,
        @Size(max = 150) String calle,
        @Size(max = 30) String numeroExterior,
        @Size(max = 30) String numeroInterior,
        @Size(max = 120) String colonia,
        @Size(max = 100) String ciudad,
        @Size(max = 100) String estado,
        @Size(max = 15) String codigoPostal,
        @Size(max = 2) String pais,
        @Size(max = 120) String ocupacion,
        @Size(max = 180) String lugarTrabajo,
        @Size(max = 30) String telefonoTrabajo,
        boolean activo,
        Long version
) {
}
