package escuela.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CancelacionEventoForm {
    @NotNull private Long version;
    @NotBlank @Size(max = 2000) private String motivo;
}
