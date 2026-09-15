package escuela.finanzas.dto.request;

import escuela.finanzas.entity.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PagoRequest(
        @NotNull Long institucionId,
        @NotNull Long plantelRegistroId,
        @NotNull Long tutorId,
        @Size(max = 180) String nombrePagador,
        @NotBlank @Size(max = 50) String folio,
        @NotNull Instant fechaPago,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal monto,
        @NotBlank @Pattern(regexp = "[A-Za-z]{3}") String moneda,
        @NotNull MetodoPago metodo,
        Long cuentaDeclaradaId,
        @Size(max = 150) String referencia,
        @Size(max = 4000) String observaciones,
        @NotNull @Size(max = 100) List<@Valid SolicitudAplicacionPagoRequest> solicitudes
) {
}
