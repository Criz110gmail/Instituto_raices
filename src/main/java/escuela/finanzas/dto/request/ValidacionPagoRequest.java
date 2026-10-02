package escuela.finanzas.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ValidacionPagoRequest(
        @NotNull Long cuentaDestinoId,
        @Size(max = 2000) String motivoCambioCuenta,
        @NotNull Long version
) { }
