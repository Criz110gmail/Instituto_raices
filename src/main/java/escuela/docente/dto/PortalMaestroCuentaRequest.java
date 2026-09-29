package escuela.docente.dto;

import jakarta.validation.constraints.*;

public record PortalMaestroCuentaRequest(@NotBlank @Size(max=80) String username,
                                         @NotBlank @Email @Size(max=254) String email) { }
