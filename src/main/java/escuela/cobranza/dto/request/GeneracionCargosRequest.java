package escuela.cobranza.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GeneracionCargosRequest(
        @NotNull Long institucionId,
        Long plantelId,
        @NotNull LocalDate fechaCorte
) {
}
