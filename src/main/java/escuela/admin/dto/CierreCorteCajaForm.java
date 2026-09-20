package escuela.admin.dto;

import escuela.finanzas.dto.request.CierreCorteCajaRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CierreCorteCajaForm {
    @NotNull @DecimalMin("0.00") @Digits(integer = 17, fraction = 2)
    private BigDecimal efectivoDeclarado;
    @Size(max = 2000) private String justificacionDiferencia;
    @Size(max = 2000) private String observaciones;
    @NotBlank @Size(max = 120) private String claveIdempotencia = UUID.randomUUID().toString();
    @NotNull private Long version;

    public CierreCorteCajaRequest request() {
        return new CierreCorteCajaRequest(efectivoDeclarado, justificacionDiferencia,
                observaciones, claveIdempotencia, version);
    }
}
