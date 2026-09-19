package escuela.finanzas.dto.response;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.finanzas.entity.NaturalezaMotivoFinanciero;

public record MotivoFinancieroResponse(Long id, Long institucionId, String institucionNombre,
                                       String codigo, String nombre,
                                       NaturalezaMotivoFinanciero naturaleza, String categoria,
                                       boolean activo, AuditoriaResponse auditoria) { }
