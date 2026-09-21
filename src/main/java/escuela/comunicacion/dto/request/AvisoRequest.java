package escuela.comunicacion.dto.request;

import java.time.LocalDateTime;

public record AvisoRequest(Long institucionId, Long plantelId, String titulo, String contenido,
                           LocalDateTime expiraLocal, Long version) { }
