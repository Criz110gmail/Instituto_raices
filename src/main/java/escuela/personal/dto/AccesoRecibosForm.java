package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class AccesoRecibosForm {
 @NotNull private Long version;
 @NotBlank private String accion;
 @Size(max=80) private String username;
 @Email @Size(max=254) private String email;
 @AssertTrue(message="Confirma la operación antes de continuar") private boolean confirmado;
}
