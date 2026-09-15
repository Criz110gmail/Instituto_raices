package escuela.cobranza.dto.request;
import escuela.cobranza.entity.*;import jakarta.validation.constraints.*;import java.math.BigDecimal;
public record PoliticaRecargoRequest(@NotNull Long conceptoCobroId,@NotNull ModalidadBeca modalidad,
 BigDecimal porcentaje,BigDecimal montoFijo,@Size(max=3)String moneda,
 @Min(0)@Max(365)int diasGracia,@NotNull PeriodicidadRecargo periodicidad,
 @NotNull TipoLimiteRecargo tipoLimite,BigDecimal valorLimite,
 boolean generacionAutomatica,boolean activo,Long version){}
