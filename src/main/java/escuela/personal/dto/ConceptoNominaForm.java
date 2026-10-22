package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ConceptoNominaForm {
 @NotBlank(message="Captura el código.") @Size(max=50) @Pattern(regexp="[A-Za-z0-9][A-Za-z0-9_-]{0,49}",message="Usa letras, números, guion o guion bajo, sin espacios.")
 private String codigo;
 @NotBlank(message="Captura el nombre.") @Size(max=150) private String nombre;
 @Size(max=2000) private String descripcion;
 @NotBlank(message="Selecciona el tipo de concepto.") @Pattern(regexp="PERCEPCION|DEDUCCION|APORTACION_PATRONAL",message="Selecciona un tipo válido.") private String naturaleza;
 @NotBlank(message="Selecciona la forma de aplicación.") @Pattern(regexp="HABITUAL|OCASIONAL",message="Selecciona una aplicación válida.") private String aplicacion;
 private boolean activo=true;
 @NotNull @PositiveOrZero private Long version=0L;
}
