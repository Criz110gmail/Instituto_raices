package escuela.admin.dto;

import escuela.finanzas.dto.request.AperturaCorteCajaRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AperturaCorteCajaForm {
    @NotNull private Long institucionId;
    @NotNull private Long cuentaId;
    private String cuentaTexto;
    @Size(max = 2000) private String observaciones;
    @NotBlank @Size(max = 120) private String claveIdempotencia = UUID.randomUUID().toString();

    public AperturaCorteCajaRequest request() {
        return new AperturaCorteCajaRequest(institucionId, cuentaId, observaciones, claveIdempotencia);
    }
}
