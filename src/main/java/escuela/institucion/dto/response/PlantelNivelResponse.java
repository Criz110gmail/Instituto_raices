package escuela.institucion.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record PlantelNivelResponse(
        Long id, Long plantelId, Long nivelEducativoId, String claveCentroTrabajo,
        boolean activo, AuditoriaResponse auditoria
) {
}
