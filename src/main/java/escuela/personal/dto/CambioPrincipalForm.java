package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class CambioPrincipalForm {
    @NotNull private LocalDate fecha;
    @NotNull private Long plantelId;
    @NotNull private Long origenId;
    @NotNull private Long versionOrigen;
    private Long destinoId;
    private Long versionDestino;
    private boolean conservarAnterior;
    @NotBlank @Size(max=2000) private String motivo;
}
