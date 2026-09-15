package escuela.cobranza.dto.response;

import escuela.common.dto.response.AuditoriaResponse;

public record TipoBecaResponse(Long id, Long institucionId, String institucionNombre,
                               String codigo, String nombre, String descripcion,
                               boolean activo, AuditoriaResponse auditoria) { }
