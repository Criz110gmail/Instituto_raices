package escuela.admin.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class TransferenciaVencidaForm {
    @NotNull private Long version;
    private boolean permitir;
    @NotBlank @Size(max=2000) private String motivo;
}
