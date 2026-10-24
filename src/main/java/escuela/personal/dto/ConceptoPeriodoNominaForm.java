package escuela.personal.dto;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Getter @Setter
public class ConceptoPeriodoNominaForm {
 @NotNull @Positive private Long periodoId;
 @NotBlank @Size(max=450) private String periodoTexto;
 @NotNull @Positive private Long conceptoId;
 @NotBlank @Size(max=450) private String conceptoTexto;
 @NotNull @PositiveOrZero private Long conceptoVersion;
 @NotNull @PositiveOrZero private Long laboralVersion;
 @NotNull @PositiveOrZero private Long calendarioVersion;
 @NotBlank @Pattern(regexp="[a-f0-9]{64}") private String selloPeriodo;
 @NotNull @DecimalMin("0.01") @Digits(integer=17,fraction=2) private BigDecimal importe;
 @NotBlank @Size(max=2000) private String motivo;
}
