package escuela.tutor.dto.response;

import escuela.tutor.entity.TipoIdentificacionTutor;

import java.time.Instant;

public record IdentificacionTutorResponse(
        Long id,
        TipoIdentificacionTutor tipo,
        String tipoEtiqueta,
        String nombreOriginal,
        String tipoMime,
        long tamanoBytes,
        Instant cargadaEn,
        Instant retiradaEn,
        boolean actual
) {}
