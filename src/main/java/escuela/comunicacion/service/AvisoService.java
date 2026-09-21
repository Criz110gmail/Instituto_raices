package escuela.comunicacion.service;

import escuela.comunicacion.dto.request.AvisoRequest;
import escuela.comunicacion.dto.response.AvisoResponse;

public interface AvisoService {
    AvisoResponse crear(AvisoRequest request);
    AvisoResponse actualizar(Long id, AvisoRequest request);
    AvisoResponse publicar(Long id, Long version);
    AvisoResponse retirar(Long id, Long version, String motivo);
    AvisoResponse obtener(Long id);
}
