package escuela.compras.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class CompraPartidaForm {
    @NotBlank(message="Describe el producto o servicio") @Size(max=300) private String descripcion;
    @NotNull @DecimalMin(value="0.001",message="La cantidad debe ser mayor a cero") @Digits(integer=9,fraction=3) private BigDecimal cantidad=BigDecimal.ONE;
    @NotNull @DecimalMin(value="0.00") @Digits(integer=17,fraction=2) private BigDecimal precioUnitario=BigDecimal.ZERO;
    @NotNull @DecimalMin(value="0.00") @DecimalMax(value="100.00") @Digits(integer=3,fraction=2) private BigDecimal porcentajeImpuesto=BigDecimal.ZERO;
}
