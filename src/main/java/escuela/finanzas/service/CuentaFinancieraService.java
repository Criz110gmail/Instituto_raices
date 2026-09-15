package escuela.finanzas.service;

import escuela.finanzas.dto.request.CuentaFinancieraRequest;
import escuela.finanzas.dto.response.CuentaFinancieraResponse;

public interface CuentaFinancieraService {
    CuentaFinancieraResponse crear(CuentaFinancieraRequest request);
    CuentaFinancieraResponse actualizar(Long id, CuentaFinancieraRequest request);
    CuentaFinancieraResponse obtener(Long id);
    void desactivar(Long id, Long version);
}
