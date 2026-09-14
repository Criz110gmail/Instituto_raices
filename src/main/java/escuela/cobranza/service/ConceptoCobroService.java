package escuela.cobranza.service;

import escuela.cobranza.dto.request.ConceptoCobroRequest;
import escuela.cobranza.dto.response.ConceptoCobroResponse;

public interface ConceptoCobroService {
    ConceptoCobroResponse crear(ConceptoCobroRequest request);
    ConceptoCobroResponse actualizar(Long id, ConceptoCobroRequest request);
    ConceptoCobroResponse obtener(Long id);
    void desactivar(Long id, Long version);
}
