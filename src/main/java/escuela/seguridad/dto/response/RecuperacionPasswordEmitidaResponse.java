package escuela.seguridad.dto.response;

import java.time.Instant;

public record RecuperacionPasswordEmitidaResponse(String token, Instant expiraEn) {
}
