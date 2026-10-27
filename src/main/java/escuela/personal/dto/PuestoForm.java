package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
@Getter @Setter public class PuestoForm {
 @NotBlank @Size(max=50) private String codigo;
 @NotBlank @Size(max=150) private String nombre;
 @Size(max=2000) private String descripcion;
 @NotNull(message="Captura las horas semanales.") @DecimalMin("0.01") @DecimalMax("168") @Digits(integer=3,fraction=2) private java.math.BigDecimal horasSemanales;
 @NotNull(message="Captura el sueldo mensual.") @DecimalMin("0.01") @Digits(integer=17,fraction=2) private java.math.BigDecimal sueldoMensual;
 private boolean activo=true;
 private Long version;
}
