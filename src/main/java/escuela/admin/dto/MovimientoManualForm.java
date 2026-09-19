package escuela.admin.dto;

import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.entity.DireccionMovimiento;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MovimientoManualForm {
    @NotNull private Long institucionId;
    @NotNull private Long cuentaId;
    private String cuentaTexto;
    private Long plantelOperacionId;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime fechaOperacion;
    @NotNull private DireccionMovimiento direccion;
    @NotNull private Long motivoFinancieroId;
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) private BigDecimal monto;
    @NotBlank @Size(max = 250) private String concepto;
    @Size(max = 150) private String referencia;
    @Size(max = 180) private String terceroNombre;
    @NotBlank @Size(max = 120) private String claveIdempotencia = UUID.randomUUID().toString();

    public MovimientoManualRequest request() {
        return new MovimientoManualRequest(institucionId, cuentaId, plantelOperacionId,
                fechaOperacion, direccion, motivoFinancieroId, monto, concepto,
                referencia, terceroNombre, claveIdempotencia);
    }
}
