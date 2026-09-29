package escuela.docente.dto;

import jakarta.validation.constraints.*;

public record MaestroRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max=50) String numeroEmpleado,
        @NotBlank @Size(max=150) String nombres,
        @NotBlank @Size(max=100) String primerApellido,
        @Size(max=100) String segundoApellido,
        @NotBlank @Email @Size(max=254) String email,
        @Size(max=30) String telefono,
        Long version) { }
