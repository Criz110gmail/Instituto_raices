package escuela.admin.dto;

import escuela.finanzas.dto.request.SolicitudAplicacionPagoRequest;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SolicitudAplicacionPagoForm {
    @NotNull private Long cargoId;
    private String cargoEtiqueta;
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2)
    private BigDecimal montoSolicitado;

    public SolicitudAplicacionPagoRequest request() {
        return new SolicitudAplicacionPagoRequest(cargoId, montoSolicitado);
    }
}
