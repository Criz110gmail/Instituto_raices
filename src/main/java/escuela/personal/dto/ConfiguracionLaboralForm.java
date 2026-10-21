package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;import lombok.Setter;
import java.math.BigDecimal;
@Getter @Setter public class ConfiguracionLaboralForm {
 @NotBlank @Size(max=50) private String codigo;
 @NotBlank @Size(max=150) private String nombre;
 @Size(max=2000) private String descripcion;
 private boolean activo=true;
 private Long version,puestoId,jornadaId;
 private String puestoTexto,jornadaTexto;
 private BigDecimal horasSemanales,importeBruto;
 private String base="MENSUAL",frecuencia="MENSUAL";
}
