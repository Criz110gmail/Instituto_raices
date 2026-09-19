package escuela.admin.dto;

import escuela.finanzas.dto.request.ReversionFinancieraRequest;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReversionFinancieraForm {
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") private LocalDateTime fecha;
    @NotBlank @Size(max = 2000) private String motivo;
    @NotBlank @Size(max = 120) private String claveIdempotencia = UUID.randomUUID().toString();
    @NotNull private Long version;

    public ReversionFinancieraRequest request() {
        return new ReversionFinancieraRequest(fecha, motivo, claveIdempotencia, version);
    }
}
