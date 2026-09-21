package escuela.admin.dto;

import escuela.finanzas.dto.request.RetiroFondoRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RetiroFondoForm {
    @NotNull private Long institucionId;
    @NotNull private Long cuentaId;
    private String cuentaTexto;
    private Long plantelOperacionId;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime fechaOperacion;
    @NotNull private Long motivoFinancieroId;
    @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) private BigDecimal monto;
    @NotBlank @Size(max = 180) private String beneficiario;
    @NotBlank @Size(max = 230) private String concepto;
    @NotBlank @Size(max = 150) private String referencia;
    @Size(max = 500) private String observaciones;
    @NotBlank @Size(max = 100) private String claveIdempotencia = UUID.randomUUID().toString();

    public RetiroFondoRequest request() {
        return new RetiroFondoRequest(institucionId, cuentaId, plantelOperacionId,
                fechaOperacion, motivoFinancieroId, monto, beneficiario, concepto,
                referencia, observaciones, claveIdempotencia);
    }
}
