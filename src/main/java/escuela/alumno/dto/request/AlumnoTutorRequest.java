package escuela.alumno.dto.request;

import escuela.alumno.entity.ParentescoTutor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AlumnoTutorRequest(
        @NotNull Long alumnoId,
        @NotNull Long tutorId,
        @NotNull ParentescoTutor parentesco,
        @Size(max = 100) String parentescoOtro,
        boolean contactoPrincipal,
        boolean responsableFinanciero,
        boolean puedeAutorizar,
        boolean puedeRecoger,
        boolean puedeVerFinanzas,
        boolean puedeRecibirNotificaciones,
        @NotNull LocalDate fechaInicio,
        LocalDate fechaFin,
        @Size(max = 4000) String observaciones,
        boolean activo,
        Long version
) {
}
