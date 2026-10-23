package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class AplicarNominaForm {
 @NotNull(message="Selecciona la cuenta de origen.") @Positive private Long cuentaId;
 private String cuentaTexto;
 @NotNull(message="Selecciona un motivo financiero de egreso.") @Positive private Long motivoPagoId;
 private String motivoPagoTexto;
 @NotBlank(message="Explica la aplicación de la nómina.") @Size(max=2000) private String motivo;
 @NotNull @PositiveOrZero private Long version;
}
