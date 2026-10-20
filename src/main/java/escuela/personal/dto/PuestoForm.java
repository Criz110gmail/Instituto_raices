package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter public class PuestoForm {
 @NotBlank @Size(max=50) private String codigo;
 @NotBlank @Size(max=150) private String nombre;
 @Size(max=2000) private String descripcion;
 private boolean activo=true;
 private Long version;
}
