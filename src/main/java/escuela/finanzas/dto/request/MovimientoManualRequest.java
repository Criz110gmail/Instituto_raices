package escuela.finanzas.dto.request;

import escuela.finanzas.entity.DireccionMovimiento;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoManualRequest(
        @NotNull Long institucionId,
        @NotNull Long cuentaId,
        Long plantelOperacionId,
        @NotNull LocalDateTime fechaOperacion,
        @NotNull DireccionMovimiento direccion,
        @NotNull Long motivoFinancieroId,
        @NotNull @DecimalMin(value = "0.01") @Digits(integer = 17, fraction = 2) BigDecimal monto,
        @NotBlank @Size(max = 250) String concepto,
        @Size(max = 150) String referencia,
        @Size(max = 180) String terceroNombre,
        @NotBlank @Size(max = 120) String claveIdempotencia) { }
