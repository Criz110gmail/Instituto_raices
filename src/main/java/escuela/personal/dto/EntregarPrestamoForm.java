package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class EntregarPrestamoForm extends AplicarNominaForm {
 @NotBlank @Size(max=150) private String referencia;
 @AssertTrue(message="Confirma la entrega real del dinero.") private boolean entregaConfirmada;
}
