package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Getter @Setter
public class PagoEmpleadoNominaForm {
 @NotNull @PositiveOrZero private Long version;
 @NotNull private UUID solicitud=UUID.randomUUID();
 private Long cuentaId;
 private String cuentaTexto;
 private Long motivoPagoId;
 private String motivoPagoTexto;
 @NotBlank(message="Captura referencia o comprobante.") @Size(max=150) private String referencia;
 @NotBlank(message="Explica el motivo.") @Size(max=2000) private String motivo;
 private boolean confirmado;
}
