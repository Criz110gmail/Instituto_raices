package escuela.inscripcion.dto.request;

import escuela.inscripcion.entity.EstadoInscripcion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record InscripcionRequest(
        @NotNull Long alumnoId,
        @NotNull Long plantelId,
        @NotNull Long cicloEscolarId,
        @NotNull Long gradoId,
        @NotBlank @Size(max = 60) String numeroInscripcion,
        @NotNull LocalDate fechaInscripcion,
        @NotNull LocalDate fechaInicio,
        LocalDate fechaFin,
        @NotNull EstadoInscripcion estado,
        @Size(max = 2000) String motivoBajaCancelacion,
        Long inscripcionAnteriorId,
        @Size(max = 4000) String observaciones,
        Long version
) {
}
