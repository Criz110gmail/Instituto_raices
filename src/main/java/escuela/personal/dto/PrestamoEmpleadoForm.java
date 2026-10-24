package escuela.personal.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter @Setter
public class PrestamoEmpleadoForm {
 @NotNull(message="Selecciona una deducción con saldo.") @Positive private Long conceptoId;
 @NotBlank(message="Selecciona el concepto de las sugerencias.") @Size(max=450) private String conceptoTexto;
 @NotNull(message="Espera la consulta del concepto.") @PositiveOrZero private Long conceptoVersion;
 @NotNull(message="Captura el importe autorizado.") @DecimalMin(value="0.01",message="El importe debe ser positivo.") @Digits(integer=17,fraction=2,message="Máximo17 enteros y2 decimales.") private BigDecimal importe;
 @NotNull(message="Captura la parcialidad mensual.") @DecimalMin(value="0.01",message="La parcialidad debe ser positiva.") @Digits(integer=17,fraction=2,message="Máximo17 enteros y2 decimales.") private BigDecimal parcialidad;
 @NotNull(message="Indica el primer mes de descuento.") @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate primerDescuento;
 @NotBlank(message="Explica el motivo del préstamo.") @Size(max=2000) private String motivo;
 @NotNull @PositiveOrZero private Long version=0L;
 @NotNull private UUID solicitud=UUID.randomUUID();
}
