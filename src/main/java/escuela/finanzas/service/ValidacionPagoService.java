package escuela.finanzas.service;

import escuela.finanzas.dto.request.RechazoPagoRequest;
import escuela.finanzas.dto.request.ValidacionPagoRequest;
import escuela.finanzas.dto.response.PagoResponse;

public interface ValidacionPagoService {
    PagoResponse validar(Long pagoId, ValidacionPagoRequest request);
    PagoResponse rechazar(Long pagoId, RechazoPagoRequest request);
}
