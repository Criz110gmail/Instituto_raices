package escuela.finanzas.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChequeraForm {
 @NotBlank @Size(max=150) private String nombre;
 @NotNull @Min(1) @Max(999999999999L) private Long folioInicial;
 @NotNull @Min(1) @Max(999999999999L) private Long folioFinal;
 private boolean activo=true;
 @NotBlank @Size(max=2000) private String motivo;
 @NotNull @Min(0) private Long version=0L;
}
