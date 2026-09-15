package escuela.cobranza.service;
import escuela.cobranza.dto.request.TipoBecaRequest;
import escuela.cobranza.dto.response.TipoBecaResponse;
public interface TipoBecaService {
    TipoBecaResponse crear(TipoBecaRequest request); TipoBecaResponse actualizar(Long id, TipoBecaRequest request);
    TipoBecaResponse obtener(Long id); void desactivar(Long id, Long version);
}
