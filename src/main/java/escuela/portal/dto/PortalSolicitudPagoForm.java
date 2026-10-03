package escuela.portal.dto;

import escuela.finanzas.dto.request.SolicitudAplicacionPagoRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class PortalSolicitudPagoForm {
    @NotNull private Long cargoId;
    private String cargoEtiqueta;
    private BigDecimal montoSolicitado;
    public SolicitudAplicacionPagoRequest request() { return new SolicitudAplicacionPagoRequest(cargoId, montoSolicitado); }
}
