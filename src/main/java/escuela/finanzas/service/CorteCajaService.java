package escuela.finanzas.service;

import escuela.finanzas.dto.request.AperturaCorteCajaRequest;
import escuela.finanzas.dto.request.CierreCorteCajaRequest;
import escuela.finanzas.dto.response.CorteCajaResponse;

public interface CorteCajaService {
    CorteCajaResponse abrir(AperturaCorteCajaRequest request);
    CorteCajaResponse cerrar(Long corteId, CierreCorteCajaRequest request);
    CorteCajaResponse obtener(Long corteId);
}
