package escuela.academico.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record GradoResponse(
        Long id, Long nivelEducativoId, String codigo, String nombre, int orden,
        boolean activo, AuditoriaResponse auditoria
) {
}
