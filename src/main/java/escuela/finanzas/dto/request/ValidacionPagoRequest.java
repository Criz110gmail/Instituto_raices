package escuela.finanzas.dto.request;

import jakarta.validation.constraints.NotNull;

public record ValidacionPagoRequest(@NotNull Long cuentaDestinoId, @NotNull Long version) { }
