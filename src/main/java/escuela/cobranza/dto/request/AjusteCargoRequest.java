package escuela.cobranza.dto.request;

import escuela.cobranza.entity.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record AjusteCargoRequest(@NotNull Long cargoId, @NotNull TipoAjusteCargo tipo,
                                 @NotNull EfectoAjusteCargo efecto,
                                 @NotNull @DecimalMin("0.01") @Digits(integer = 12, fraction = 2) BigDecimal monto,
                                 @NotBlank @Size(max = 2000) String motivo,
                                 @NotNull LocalDate fechaEfectiva) { }
