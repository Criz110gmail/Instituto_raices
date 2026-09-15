package escuela.finanzas.dto.response;

import java.time.Instant;

public record ComprobantePagoResponse(Long id, String nombreOriginal, String tipoMime,
                                      long tamanoBytes, Instant creadoEn) {
}
