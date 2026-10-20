package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
@Getter @Setter public class LaboralForm {
 @Size(max=150) private String puesto;
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaIngreso;
 @NotBlank @Pattern(regexp="POR_CONFIRMAR|ACTIVO|BAJA") private String situacion="POR_CONFIRMAR";
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaBaja;
 @Size(max=2000) private String motivoBaja;
 private Long version;
}
