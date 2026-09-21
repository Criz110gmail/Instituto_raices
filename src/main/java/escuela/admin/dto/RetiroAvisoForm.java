package escuela.admin.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RetiroAvisoForm {
    @NotNull private Long version;
    @NotBlank @Size(max=1000) private String motivo;
}
