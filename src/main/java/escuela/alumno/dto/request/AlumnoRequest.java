package escuela.alumno.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlumnoRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String matricula,
        @NotBlank @Size(max = 150) String nombres,
        @NotBlank @Size(max = 100) String primerApellido,
        @Size(max = 100) String segundoApellido,
        @Pattern(regexp = "^$|^[A-Za-z0-9]{18}$", message = "La CURP debe contener 18 caracteres alfanuméricos") String curp,
        @NotNull @PastOrPresent LocalDate fechaNacimiento,
        @Size(max = 30) String sexo,
        @Size(max = 150) String lugarNacimiento,
        @Size(max = 80) String nacionalidad,
        @Size(max = 30) String telefono,
        @Email @Size(max = 254) String email,
        @Size(max = 150) String calle,
        @Size(max = 30) String numeroExterior,
        @Size(max = 30) String numeroInterior,
        @Size(max = 120) String colonia,
        @Size(max = 100) String ciudad,
        @Size(max = 100) String estado,
        @Size(max = 15) String codigoPostal,
        @Size(max = 2) String pais,
        @NotNull @PastOrPresent LocalDate fechaIngreso,
        @Size(max = 4000) String observaciones,
        boolean activo,
        Long version
) {
}
