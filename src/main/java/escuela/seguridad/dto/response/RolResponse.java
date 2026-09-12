package escuela.seguridad.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record RolResponse(
        Long id,
        Long institucionId,
        String codigo,
        String nombre,
        String descripcion,
        boolean activo,
        AuditoriaResponse auditoria
) {
}
