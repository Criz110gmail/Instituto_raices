package escuela.academico.dto.response;

import escuela.academico.entity.EstadoAcademico;
import escuela.common.dto.response.AuditoriaResponse;

import java.time.LocalDate;

public record CicloEscolarResponse(
        Long id, Long institucionId, String codigo, String nombre, LocalDate fechaInicio,
        LocalDate fechaFin, EstadoAcademico estado, boolean predeterminado,
        AuditoriaResponse auditoria
) {
}
