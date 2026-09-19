package escuela.finanzas.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DevolucionPagoRequest(
        @NotNull Long pagoId,
        @NotNull Long cuentaOrigenId,
        @NotNull LocalDateTime fecha,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal monto,
        @NotBlank @Size(max = 2000) String motivo,
        @NotBlank @Size(max = 180) String beneficiario,
        @Size(max = 150) String referencia,
        List<Long> aplicacionIdsRevertir,
        @NotBlank @Size(max = 120) String claveIdempotencia,
        Long pagoVersion) {
    public DevolucionPagoRequest {
        aplicacionIdsRevertir = aplicacionIdsRevertir == null ? List.of() : List.copyOf(aplicacionIdsRevertir);
    }
}
