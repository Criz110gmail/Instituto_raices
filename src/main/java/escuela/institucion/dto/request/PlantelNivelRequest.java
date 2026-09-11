package escuela.institucion.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlantelNivelRequest(
        @NotNull Long plantelId,
        @NotNull Long nivelEducativoId,
        @Size(max = 30) String claveCentroTrabajo,
        boolean activo,
        Long version
) {
}
