package escuela.compras.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CompraRespaldoForm {
    @NotNull private Long version;
    @NotBlank @Pattern(regexp="SIN_COMPROBANTE|TICKET_RECIBO|CFDI") private String tipo="TICKET_RECIBO";
    @Size(max=2000) private String justificacion;
    private boolean confirmado;
}
