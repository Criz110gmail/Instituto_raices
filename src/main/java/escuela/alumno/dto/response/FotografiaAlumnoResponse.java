package escuela.alumno.dto.response;

import java.time.Instant;

public record FotografiaAlumnoResponse(
        Long id,
        String nombreOriginal,
        String tipoMime,
        long tamanoBytes,
        Instant asignadaEn,
        Instant retiradaEn,
        boolean actual
) {
}
