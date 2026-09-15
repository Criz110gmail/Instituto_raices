package escuela.cobranza.dto.request;

import jakarta.validation.constraints.*;

public record TipoBecaRequest(@NotNull Long institucionId,
                              @NotBlank @Size(max = 50) String codigo,
                              @NotBlank @Size(max = 150) String nombre,
                              @Size(max = 2000) String descripcion,
                              boolean activo, Long version) { }
