package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter public class CalendarioEmpleadoForm {
 @NotNull(message="Selecciona un calendario de la ayuda.") @Positive private Long calendarioId;
 private String calendarioTexto;
 @NotNull @PositiveOrZero private Long version;
 @NotBlank(message="Explica el motivo del cambio.") @Size(max=2000) private String motivo;
}
