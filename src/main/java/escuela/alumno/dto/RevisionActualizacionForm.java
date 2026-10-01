package escuela.alumno.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevisionActualizacionForm {
    @NotBlank(message = "Escribe una respuesta para la familia")
    @Size(max = 2000, message = "La respuesta admite máximo 2000 caracteres")
    private String respuesta;
    @NotNull private Long version;
}
