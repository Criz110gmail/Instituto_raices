package escuela.finanzas.dto.request;

import escuela.finanzas.entity.TipoCuentaFinanciera;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuentaFinancieraRequest(
        @NotNull Long institucionId,
        Long plantelId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull TipoCuentaFinanciera tipo,
        @Size(max = 120) String bancoNombre,
        @Size(max = 150) String titular,
        @Size(max = 34) String numeroCuenta,
        @Pattern(regexp = "^$|[0-9]{18}", message = "La CLABE debe contener exactamente 18 dígitos") String clabe,
        @NotBlank @Size(min = 3, max = 3) String moneda,
        @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2) BigDecimal saldoInicial,
        @NotNull LocalDate fechaSaldoInicial,
        boolean activo,
        Long version
) { }
