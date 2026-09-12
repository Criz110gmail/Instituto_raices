package escuela.seguridad.dto.response;

import java.time.Instant;

public record InvitacionEmitidaResponse(String token, Instant expiraEn) {
}
