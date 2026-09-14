package escuela.inscripcion.dto.response;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.inscripcion.entity.EstadoInscripcion;

import java.time.LocalDate;

public record InscripcionResponse(
        Long id,
        Long institucionId,
        Long alumnoId,
        String alumnoMatricula,
        String alumnoNombre,
        Long plantelId,
        String plantelNombre,
        Long cicloEscolarId,
        String cicloNombre,
        Long gradoId,
        String gradoNombre,
        String numeroInscripcion,
        LocalDate fechaInscripcion,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoInscripcion estado,
        String motivoBajaCancelacion,
        Long inscripcionAnteriorId,
        String observaciones,
        AuditoriaResponse auditoria
) {
}
