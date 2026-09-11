package escuela.academico.dto.response;

import escuela.academico.entity.Turno;
import escuela.common.dto.response.AuditoriaResponse;

public record GrupoResponse(
        Long id, Long plantelId, Long cicloEscolarId, Long gradoId, String nombre,
        Turno turno, String codigo, String aula, Integer capacidad, boolean activo,
        AuditoriaResponse auditoria
) {
}
