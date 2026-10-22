package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.*;

@Getter @Setter
public class BorradorNominaForm {
 @NotNull @Positive private Long periodoId;
 @NotBlank @Pattern(regexp="[a-f0-9]{64}") private String selloPeriodo;
 @NotBlank(message="Captura un nombre para el borrador.") @Size(max=150) private String nombre;
 @NotBlank(message="Explica el motivo de la preparación o actualización.") @Size(max=2000) private String motivo;
 @NotEmpty(message="Selecciona al menos un empleado listo para preparar.") @Size(max=100,message="Prepara hasta 100 empleados por borrador; puedes crear otro para los demás.") private List<@Pattern(regexp="[0-9]+:[a-f0-9]{64}")String> seleccion=new ArrayList<>();
}
