package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
@Getter @Setter public class CondicionSalarialForm {
 @NotNull(message="Selecciona un puesto de la ayuda.") private Long puestoId;
 @NotBlank @Size(max=250) private String puestoTexto;
 @NotNull @DecimalMin(value="0.01",message="El sueldo bruto debe ser mayor que cero.") @Digits(integer=17,fraction=2) private BigDecimal importeBruto;
 @NotBlank @Pattern(regexp="MENSUAL|SEMANAL|DIARIA|POR_HORA") private String base="MENSUAL";
 @NotBlank @Pattern(regexp="MENSUAL|QUINCENAL|SEMANAL") private String frecuencia="MENSUAL";
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaInicio;
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaFin;
 @NotBlank(message="Explica por qué registras esta condición salarial.") @Size(max=2000) private String motivo;
 @NotNull private Long versionLaboral;
 @NotNull private Long revision;
}
