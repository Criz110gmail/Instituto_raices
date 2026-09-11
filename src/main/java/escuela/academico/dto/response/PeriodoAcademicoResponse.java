package escuela.academico.dto.response;

import escuela.academico.entity.EstadoAcademico;
import escuela.academico.entity.TipoPeriodoAcademico;
import escuela.common.dto.response.AuditoriaResponse;

import java.time.LocalDate;

public record PeriodoAcademicoResponse(
        Long id, Long cicloEscolarId, Long nivelEducativoId, String codigo, String nombre,
        TipoPeriodoAcademico tipo, int orden, LocalDate fechaInicio, LocalDate fechaFin,
        EstadoAcademico estado, String observaciones, AuditoriaResponse auditoria
) {
}
