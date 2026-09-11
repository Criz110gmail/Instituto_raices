package escuela.academico.dto.request;

import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.TipoPeriodoAcademico;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PeriodoAcademicoRequest(
        @NotNull Long cicloEscolarId,
        @NotNull Long nivelEducativoId,
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nombre,
        @NotNull TipoPeriodoAcademico tipo,
        @Positive int orden,
        @NotNull LocalDate fechaInicio,
        @NotNull LocalDate fechaFin,
        @NotNull EstadoAcademico estado,
        String observaciones,
        Long version
) {
}
