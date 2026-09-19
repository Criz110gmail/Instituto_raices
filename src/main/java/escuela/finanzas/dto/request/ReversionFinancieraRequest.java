package escuela.finanzas.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ReversionFinancieraRequest(
        @NotNull LocalDateTime fecha,
        @NotBlank @Size(max = 2000) String motivo,
        @NotBlank @Size(max = 120) String claveIdempotencia,
        @NotNull Long version) { }
