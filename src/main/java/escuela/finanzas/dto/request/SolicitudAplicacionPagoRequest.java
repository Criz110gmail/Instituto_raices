package escuela.finanzas.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SolicitudAplicacionPagoRequest(
        @NotNull Long cargoId,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal montoSolicitado
) {
}
