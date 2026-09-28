package escuela.academico.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record MateriaResponse(
        Long id, Long institucionId, String codigo, String nombre, String descripcion,
        boolean activo, long planesActivos, AuditoriaResponse auditoria
) {}

