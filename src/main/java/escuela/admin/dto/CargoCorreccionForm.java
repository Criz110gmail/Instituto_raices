package escuela.admin.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
@Getter @Setter
public class CargoCorreccionForm {
    @NotNull private Long version;
    @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaVencimiento;
    @NotBlank @Size(max=2000) private String motivo;
    @AssertTrue(message="Confirma que revisaste los datos del reemplazo") private boolean confirmacion;
}
