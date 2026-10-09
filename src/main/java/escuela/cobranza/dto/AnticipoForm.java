package escuela.cobranza.dto;
import lombok.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.*;
import java.math.*;
import java.util.*;
@Getter @Setter
public class AnticipoForm {
 @NotNull private Long institucionId;
 @NotNull private Long tutorId;
 @NotEmpty @Size(max=100) private List<Long> cargoIds=new ArrayList<>();
 @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaLimite;
 @NotBlank @Pattern(regexp="CONSERVAR|SUSTITUIR") private String politicaBeca="CONSERVAR";
 @NotBlank @Pattern(regexp="MENSUALIDAD|PORCENTAJE|MONTO") private String tipoBeneficio="MENSUALIDAD";
 @Digits(integer=12,fraction=4) private BigDecimal valor;
 private Long cargoBonificadoId;
 @NotBlank @Size(max=2000) private String motivo;
 @Size(max=64) private String huella;
}
