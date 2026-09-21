package escuela.finanzas.service;

import escuela.finanzas.dto.request.CancelacionPagoRequest;
import escuela.finanzas.dto.response.PagoResponse;

public interface CancelacionPagoService {
    PagoResponse cancelar(Long pagoId, CancelacionPagoRequest request);
}
