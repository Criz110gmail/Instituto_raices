package escuela.finanzas.service;

import escuela.finanzas.dto.request.MovimientoManualRequest;
import escuela.finanzas.dto.response.MovimientoFinancieroResponse;

public interface MovimientoManualService {
    MovimientoFinancieroResponse registrar(MovimientoManualRequest request);
    MovimientoFinancieroResponse registrarNomina(MovimientoManualRequest request);
    MovimientoFinancieroResponse registrarPrestamo(MovimientoManualRequest request);
}
