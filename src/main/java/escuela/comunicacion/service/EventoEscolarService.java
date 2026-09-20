package escuela.comunicacion.service;

import escuela.comunicacion.dto.request.EventoEscolarRequest;
import escuela.comunicacion.dto.response.EventoEscolarResponse;

public interface EventoEscolarService {
    EventoEscolarResponse crear(EventoEscolarRequest request);
    EventoEscolarResponse actualizar(Long id, EventoEscolarRequest request);
    EventoEscolarResponse publicar(Long id, Long version);
    EventoEscolarResponse cancelar(Long id, Long version, String motivo);
    EventoEscolarResponse obtener(Long id);
}
