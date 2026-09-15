package escuela.cobranza.service;
import escuela.cobranza.dto.request.AjusteCargoRequest;
import escuela.cobranza.dto.response.AjusteCargoResponse;
import java.util.List;
public interface AjusteCargoService {
    AjusteCargoResponse crear(AjusteCargoRequest request); AjusteCargoResponse reversar(Long id, Long version, String motivo);
    AjusteCargoResponse obtener(Long id); List<AjusteCargoResponse> listarPorCargo(Long cargoId);
}
