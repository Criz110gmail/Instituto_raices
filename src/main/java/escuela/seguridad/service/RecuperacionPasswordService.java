package escuela.seguridad.service;

import escuela.seguridad.dto.request.RestablecimientoPasswordRequest;
import escuela.seguridad.dto.response.RecuperacionPasswordEmitidaResponse;

import java.time.Duration;

public interface RecuperacionPasswordService {
    RecuperacionPasswordEmitidaResponse emitir(Long usuarioId, Duration vigencia);
    void restablecer(RestablecimientoPasswordRequest request);
}
