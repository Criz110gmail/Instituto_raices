package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
@Getter @Setter public class PlantelEmpleadoForm {
 @NotNull private Long plantelId;
 @Size(max=260) private String plantelTexto;
 @NotNull private LocalDate fechaInicio;
 private LocalDate fechaFin;
 private boolean principal;
}
