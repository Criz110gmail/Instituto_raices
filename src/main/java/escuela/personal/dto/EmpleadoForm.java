package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter public class EmpleadoForm {
 @NotNull private Long institucionId;
 @NotBlank @Size(max=50) private String codigo;
 @NotBlank @Size(max=150) private String nombres;
 @NotBlank @Size(max=100) private String primerApellido;
 @Size(max=100) private String segundoApellido;
 @Email @Size(max=254) private String email;
 @Size(max=30) private String telefono;
 private Long version;
}
