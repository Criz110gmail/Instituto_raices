package escuela.inscripcion.dto.response;

import escuela.academico.entity.Turno;
import escuela.common.dto.response.AuditoriaResponse;

import java.time.LocalDate;

public record AsignacionGrupoResponse(
        Long id,
        Long inscripcionId,
        Long grupoId,
        String grupoNombre,
        String grupoCodigo,
        Turno turno,
        String aula,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String motivo,
        AuditoriaResponse auditoria
) {
}
