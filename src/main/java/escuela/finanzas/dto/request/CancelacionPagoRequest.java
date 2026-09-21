package escuela.finanzas.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CancelacionPagoRequest(@NotNull Long version,
                                     @NotBlank @Size(max = 2000) String motivo) { }
