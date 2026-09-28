package escuela.alumno.dto.response;

import escuela.alumno.entity.TipoDocumentoAlumno;

import java.time.Instant;
import java.time.LocalDate;

public record DocumentoAlumnoResponse(
        Long id,
        TipoDocumentoAlumno tipo,
        String tipoEtiqueta,
        String descripcion,
        LocalDate fechaDocumento,
        LocalDate vigenteHasta,
        String nombreOriginal,
        String tipoMime,
        long tamanoBytes,
        Instant creadoEn,
        Instant retiradoEn,
        Long version,
        boolean vigente
) {}

