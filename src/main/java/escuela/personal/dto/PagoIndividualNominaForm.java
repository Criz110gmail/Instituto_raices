package escuela.personal.dto;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter
public class PagoIndividualNominaForm extends PagoMultipleNominaForm {
 private Long periodoId;
 private String periodoTexto;
 private Long empleadoId;
 private String empleadoTexto;
}
