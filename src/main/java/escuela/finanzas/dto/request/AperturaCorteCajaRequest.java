package escuela.finanzas.dto.request;

import jakarta.validation.constraints.*;

public record AperturaCorteCajaRequest(
        @NotNull Long institucionId,
        @NotNull Long cuentaId,
        @Size(max = 2000) String observaciones,
        @NotBlank @Size(max = 120) String claveIdempotencia) { }
