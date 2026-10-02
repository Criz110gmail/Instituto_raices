package escuela.cobranza.service;

import escuela.cobranza.dto.request.CargoManualRequest;
import escuela.cobranza.dto.request.GeneracionCargosRequest;
import escuela.cobranza.dto.response.CargoResponse;
import escuela.cobranza.dto.response.GeneracionCargosResponse;
import escuela.cobranza.dto.response.VistaPreviaCargosAutomaticosResponse;

public interface CargoService {
    CargoResponse crearManual(CargoManualRequest request);
    GeneracionCargosResponse generar(GeneracionCargosRequest request);
    VistaPreviaCargosAutomaticosResponse previsualizar(GeneracionCargosRequest request,
                                                        int pagina, int tamanio);
    CargoResponse generarCargoUnico(Long cuotaId);
    CargoResponse obtener(Long id);
    CargoResponse cancelar(Long id, Long version, String motivo);
}
