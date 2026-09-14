package escuela.cobranza.service;

import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.request.GeneracionCargosRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.dto.response.GeneracionCargosResponse;

public interface CargoService {
    CargoResponse crearManual(CargoManualRequest request);
    GeneracionCargosResponse generar(GeneracionCargosRequest request);
    CargoResponse obtener(Long id);
    CargoResponse cancelar(Long id, Long version, String motivo);
}
