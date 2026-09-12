package escuela.seguridad.service;

import escuela.seguridad.dto.request.ActivacionUsuarioRequest;
import escuela.seguridad.dto.response.InvitacionEmitidaResponse;

import java.time.Duration;

public interface InvitacionUsuarioService {
    InvitacionEmitidaResponse emitir(Long usuarioId, Duration vigencia);
    void activar(ActivacionUsuarioRequest request);
}
