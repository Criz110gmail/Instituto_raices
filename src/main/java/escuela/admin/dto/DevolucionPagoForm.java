package escuela.admin.dto;

import escuela.finanzas.dto.request.DevolucionPagoRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class DevolucionPagoForm {
    @NotNull private Long cuentaOrigenId;
    private String cuentaOrigenTexto;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime fecha;
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) private BigDecimal monto;
    @NotBlank @Size(max = 2000) private String motivo;
    @NotBlank @Size(max = 180) private String beneficiario;
    @Size(max = 150) private String referencia;
    private List<Long> aplicacionIdsRevertir = new ArrayList<>();
    @NotBlank @Size(max = 120) private String claveIdempotencia = UUID.randomUUID().toString();
    @NotNull private Long pagoVersion;

    public DevolucionPagoRequest request(Long pagoId) {
        return new DevolucionPagoRequest(pagoId, cuentaOrigenId, fecha, monto, motivo, beneficiario,
                referencia, aplicacionIdsRevertir, claveIdempotencia, pagoVersion);
    }
}
