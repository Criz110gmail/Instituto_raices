package escuela.alumno.dto;

import escuela.alumno.entity.*;
import java.time.Instant;

public record ActualizacionExpedienteFila(
        Long id, Long alumnoId, String alumno, String matricula, String tutor,
        TipoActualizacionExpediente tipo, EstadoActualizacionExpediente estado,
        String resumen, Instant enviadaEn, Instant revisadaEn, String respuestaAdmin,
        boolean tieneArchivo, Long version) {
}
