package escuela.finanzas.service;

import escuela.finanzas.dto.request.DevolucionPagoRequest;
import escuela.finanzas.dto.response.DevolucionPagoResponse;
import escuela.finanzas.dto.response.ResumenDevolucionPagoResponse;

public interface DevolucionPagoService {
    DevolucionPagoResponse ejecutar(DevolucionPagoRequest request);
    ResumenDevolucionPagoResponse resumen(Long pagoId);
}
