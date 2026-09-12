package escuela.seguridad.dto.request;

import escuela.seguridad.entity.AlcanceRol;
import jakarta.validation.constraints.NotNull;

public record AsignacionRolRequest(
        @NotNull Long usuarioId,
        @NotNull Long rolId,
        @NotNull AlcanceRol alcance,
        Long plantelId
) {
}
