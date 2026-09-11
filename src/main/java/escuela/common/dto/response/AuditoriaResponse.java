package escuela.common.dto.response;

import java.time.Instant;

public record AuditoriaResponse(
        Instant creadoEn,
        Long creadoPorId,
        Instant actualizadoEn,
        Long actualizadoPorId,
        Long version
) {
}
