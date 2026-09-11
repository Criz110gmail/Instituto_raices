package escuela.academico.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record NivelEducativoResponse(
        Long id, Long institucionId, String codigo, String nombre, String descripcion,
        int orden, boolean activo, AuditoriaResponse auditoria
) {
}
