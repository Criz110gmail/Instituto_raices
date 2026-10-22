package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CalendarioNominaForm {
 @NotBlank(message="Captura el código.") @Size(max=50) @Pattern(regexp="[A-Za-z0-9][A-Za-z0-9_-]{0,49}",message="Usa letras, números, guion o guion bajo, sin espacios.") private String codigo;
 @NotBlank(message="Captura el nombre del calendario.") @Size(max=150) private String nombre;
 @Size(max=2000) private String descripcion;
 @NotBlank @Pattern(regexp="MENSUAL|QUINCENAL|SEMANAL",message="Selecciona una frecuencia válida.") private String frecuencia;
 private boolean activo=true;
 @NotNull @PositiveOrZero private Long version=0L;
}
