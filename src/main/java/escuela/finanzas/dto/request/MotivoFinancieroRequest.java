package escuela.finanzas.dto.request;

import escuela.finanzas.entity.NaturalezaMotivoFinanciero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MotivoFinancieroRequest(
        @NotNull Long institucionId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull NaturalezaMotivoFinanciero naturaleza,
        @NotBlank @Size(max = 100) String categoria,
        boolean activo,
        Long version) { }
