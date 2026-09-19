package escuela.admin.dto;

import escuela.finanzas.dto.request.TransferenciaCuentaRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class TransferenciaCuentaForm {
    @NotNull private Long institucionId;
    @NotNull private Long cuentaOrigenId;
    private String cuentaOrigenTexto;
    @NotNull private Long cuentaDestinoId;
    private String cuentaDestinoTexto;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime fecha;
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) private BigDecimal monto;
    @Size(max = 150) private String referencia;
    @Size(max = 2000) private String observaciones;
    @NotBlank @Size(max = 120) private String claveIdempotencia = UUID.randomUUID().toString();

    public TransferenciaCuentaRequest request() {
        return new TransferenciaCuentaRequest(institucionId, cuentaOrigenId, cuentaDestinoId,
                fecha, monto, referencia, observaciones, claveIdempotencia);
    }
}
