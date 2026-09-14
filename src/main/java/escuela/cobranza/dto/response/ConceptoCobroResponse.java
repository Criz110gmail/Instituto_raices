package escuela.cobranza.dto.response;

import escuela.cobranza.entity.CategoriaConceptoCobro;
import escuela.common.dto.response.AuditoriaResponse;

public record ConceptoCobroResponse(
        Long id,
        Long institucionId,
        String institucionNombre,
        String codigo,
        String nombre,
        String descripcion,
        CategoriaConceptoCobro categoria,
        boolean permiteBeca,
        boolean permiteDescuento,
        boolean permiteRecargo,
        boolean activo,
        AuditoriaResponse auditoria
) {
}
