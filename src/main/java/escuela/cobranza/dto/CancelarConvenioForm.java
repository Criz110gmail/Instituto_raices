package escuela.cobranza.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter public class CancelarConvenioForm {
 @NotBlank @Size(max=2000) private String motivo;
 @NotNull private Long version;
}
