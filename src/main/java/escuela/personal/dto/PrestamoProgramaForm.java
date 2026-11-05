package escuela.personal.dto;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Getter @Setter public class PrestamoProgramaForm {
 @NotNull(message="Selecciona un empleado de las sugerencias.") @Positive private Long empleadoId;
 private String empleadoTexto;
 @NotNull(message="Selecciona la deducción del préstamo de las sugerencias.") @Positive private Long conceptoId;
 private String conceptoTexto;
 @NotNull(message="Espera la consulta del concepto o vuelve a seleccionarlo.") @PositiveOrZero private Long conceptoVersion;
 @NotNull(message="Captura el capital del préstamo.") @DecimalMin("0.00") @Digits(integer=15,fraction=2) private BigDecimal capital;
 @NotNull @DecimalMin("0") @DecimalMax("1000") @Digits(integer=4,fraction=4) private BigDecimal porcentaje=BigDecimal.ZERO;
 private boolean descontarNomina=true;
 @NotNull @DecimalMin("0.00") @Digits(integer=15,fraction=2) private BigDecimal interesNuevo=BigDecimal.ZERO;
 @NotBlank private String modalidad="SIN_INTERES";
 @NotBlank private String frecuencia="MENSUAL";
 @NotNull(message="Captura el número de cuotas.") @Min(1) @Max(600) private Integer cuotas=12;
 @NotNull(message="Indica la fecha del primer descuento.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate inicio;
 @NotBlank(message="Explica el motivo obligatorio.") @Size(max=2000) private String motivo;
 @NotNull @PositiveOrZero private Long version=0L;
 @NotNull private UUID solicitud=UUID.randomUUID();
 private String sello;
}
