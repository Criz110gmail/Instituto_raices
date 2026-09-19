package escuela.finanzas.service;

import escuela.finanzas.dto.request.ReversionFinancieraRequest;
import escuela.finanzas.dto.response.ObjetivoReversionResponse;
import escuela.finanzas.dto.response.ReversionFinancieraResponse;

public interface ReversionFinancieraService {
    ObjetivoReversionResponse obtenerMovimiento(Long movimientoId);
    ObjetivoReversionResponse obtenerTransferencia(Long transferenciaId);
    ReversionFinancieraResponse revertirMovimiento(Long movimientoId, ReversionFinancieraRequest request);
    ReversionFinancieraResponse revertirTransferencia(Long transferenciaId, ReversionFinancieraRequest request);
}
