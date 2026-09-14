package escuela.cobranza.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CargoManualRequest(
        @NotNull Long inscripcionId,
        @NotNull Long conceptoCobroId,
        @NotBlank @Size(max = 250) String descripcion,
        @NotNull LocalDate periodoCobroInicio,
        @NotNull LocalDate periodoCobroFin,
        Long periodoAcademicoId,
        @NotNull LocalDate fechaEmision,
        @NotNull LocalDate fechaVencimiento,
        @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2) BigDecimal importeOriginal,
        @NotNull @Pattern(regexp = "[A-Za-z]{3}") String moneda
) {
}
