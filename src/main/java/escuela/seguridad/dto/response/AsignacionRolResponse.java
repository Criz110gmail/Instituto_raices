package escuela.seguridad.dto.response;

import escuela.seguridad.entity.AlcanceRol;

public record AsignacionRolResponse(
        Long id,
        Long rolId,
        String rolCodigo,
        String rolNombre,
        AlcanceRol alcance,
        Long plantelId,
        String plantelNombre,
        boolean activo,
        Long version
) {
}
