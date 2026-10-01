package escuela.compras.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CancelarCompraForm {
    @NotBlank(message="Explica el motivo de la cancelación") @Size(max=2000) private String motivo;
    @NotNull private Long version;
}
