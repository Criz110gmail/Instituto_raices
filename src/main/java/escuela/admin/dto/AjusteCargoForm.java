package escuela.admin.dto;
import escuela.cobranza.dto.request.AjusteCargoRequest;import escuela.cobranza.entity.*;import jakarta.validation.constraints.*;import lombok.Getter;import lombok.Setter;import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;import java.time.LocalDate;
@Getter @Setter public class AjusteCargoForm {
 @NotNull private TipoAjusteCargo tipo=TipoAjusteCargo.DESCUENTO; @NotNull private EfectoAjusteCargo efecto=EfectoAjusteCargo.DISMINUCION;
 @NotNull @DecimalMin("0.01") @Digits(integer=12,fraction=2) private BigDecimal monto;
 @NotBlank @Size(max=2000) private String motivo; @NotNull @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) private LocalDate fechaEfectiva=LocalDate.now();
 public AjusteCargoRequest request(Long cargoId){return new AjusteCargoRequest(cargoId,tipo,efecto,monto,motivo,fechaEfectiva);}
}
