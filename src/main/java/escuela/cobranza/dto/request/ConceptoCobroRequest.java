package escuela.cobranza.dto.request;

import escuela.cobranza.entity.CategoriaConceptoCobro;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ConceptoCobroRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 2000) String descripcion,
        @NotNull CategoriaConceptoCobro categoria,
        boolean permiteBeca,
        boolean permiteDescuento,
        boolean permiteRecargo,
        boolean activo,
        Long version
) {
}
