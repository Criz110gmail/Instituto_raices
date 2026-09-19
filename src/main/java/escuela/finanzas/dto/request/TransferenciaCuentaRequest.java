package escuela.finanzas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferenciaCuentaRequest(
        @NotNull Long institucionId,
        @NotNull Long cuentaOrigenId,
        @NotNull Long cuentaDestinoId,
        @NotNull LocalDateTime fecha,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal monto,
        @Size(max = 150) String referencia,
        @Size(max = 2000) String observaciones,
        @NotBlank @Size(max = 120) String claveIdempotencia) { }
