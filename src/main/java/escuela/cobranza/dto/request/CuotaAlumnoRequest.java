package escuela.cobranza.dto.request;

import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaAlumnoRequest(
        @NotNull Long inscripcionId,
        @NotNull Long conceptoCobroId,
        @NotNull @DecimalMin("0.00") @Digits(integer = 12, fraction = 2) BigDecimal importeBase,
        @NotNull @Pattern(regexp = "[A-Za-z]{3}") String moneda,
        @NotNull FrecuenciaCuota frecuencia,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        Integer diaVencimiento,
        LocalDate fechaVencimientoUnico,
        boolean generacionAutomatica,
        @Size(max = 2000) String motivoImportePersonalizado,
        @NotNull EstadoCuota estado,
        Long version
) {
}
