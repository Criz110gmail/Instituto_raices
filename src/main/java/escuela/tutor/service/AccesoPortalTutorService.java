package escuela.tutor.service;

import escuela.tutor.dto.request.PortalTutorCuentaRequest;
import escuela.tutor.dto.response.PortalTutorCuentaResponse;

import java.util.Optional;

public interface AccesoPortalTutorService {
    Optional<PortalTutorCuentaResponse> obtener(Long tutorId);
    String sugerirUsername(Long tutorId);
    PortalTutorCuentaResponse crear(Long tutorId, PortalTutorCuentaRequest request);
    PortalTutorCuentaResponse cambiarDisponibilidad(Long tutorId, Long version, boolean activar);
}
