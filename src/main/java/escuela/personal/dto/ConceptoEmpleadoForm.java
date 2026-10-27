package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Getter @Setter
public class ConceptoEmpleadoForm {
 @NotNull(message="Selecciona el empleado de las sugerencias.") @Positive private Long empleadoId;
 @NotBlank @Size(max=450) private String empleadoTexto;
 @NotNull(message="Selecciona un concepto habitual de las sugerencias.") @Positive private Long conceptoId;
 @NotBlank @Size(max=450) private String conceptoTexto;
 @NotNull(message="Espera la consulta del concepto y empleado; vuelve a seleccionarlos si es necesario.") @PositiveOrZero private Long conceptoVersion;
 @NotNull @PositiveOrZero private Long laboralVersion;
 @NotNull(message="Captura el importe fijo.") @DecimalMin(value="0.01",message="El importe debe ser mayor que cero.") @Digits(integer=17,fraction=2) private BigDecimal importe;
 @NotNull(message="Captura el inicio de vigencia.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate inicio;
 @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fin;
 @NotBlank(message="Explica el motivo de la asignación.") @Size(max=2000) private String motivo;
 @Positive private Long anteriorId;
 @PositiveOrZero private Long anteriorVersion;
 private String frecuenciaImporte="MENSUAL";
 private String modalidad="RECURRENTE";
 @Positive private Long periodoId;
 private String periodoTexto;
 @PositiveOrZero private Long periodoVersion;
}
