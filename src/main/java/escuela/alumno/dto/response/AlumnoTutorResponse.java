package escuela.alumno.dto.response;

import escuela.alumno.entity.ParentescoTutor;
import escuela.common.dto.response.AuditoriaResponse;

import java.time.LocalDate;

public record AlumnoTutorResponse(
        Long id,
        Long institucionId,
        Long alumnoId,
        String alumnoMatricula,
        String alumnoNombre,
        Long tutorId,
        String tutorNombre,
        ParentescoTutor parentesco,
        String parentescoOtro,
        boolean contactoPrincipal,
        boolean responsableFinanciero,
        boolean puedeAutorizar,
        boolean puedeRecoger,
        boolean puedeVerFinanzas,
        boolean puedeRecibirNotificaciones,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String observaciones,
        boolean activo,
        AuditoriaResponse auditoria
) {
}
