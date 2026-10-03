package escuela.cobranza.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
@Getter @Setter
public class ConvenioPagoForm {
 @NotNull private Long institucionId;
 @NotNull private Long tutorId;
 @NotNull private Long conceptoCobroId;
 @NotEmpty @Size(max=100) private List<Long> cargoIds=new ArrayList<>();
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaAcuerdo;
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaVencimiento;
 @NotBlank @Size(max=250) private String descripcion;
 @NotBlank @Size(max=4000) private String motivo;
 @Size(max=4000) private String condiciones;
 @NotNull @DecimalMin("0.01") @Digits(integer=12,fraction=2) private BigDecimal montoAcordado;
}
