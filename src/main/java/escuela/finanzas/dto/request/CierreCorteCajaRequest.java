package escuela.finanzas.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CierreCorteCajaRequest(
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal efectivoDeclarado,
        @Size(max = 2000) String justificacionDiferencia,
        @Size(max = 2000) String observaciones,
        @NotBlank @Size(max = 120) String claveIdempotencia,
        @NotNull Long version) { }
