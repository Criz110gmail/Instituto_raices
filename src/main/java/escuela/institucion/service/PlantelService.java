package escuela.institucion.service;

import escuela.institucion.dto.request.PlantelRequest;
import escuela.institucion.dto.response.PlantelResponse;

import java.util.List;

public interface PlantelService {
    PlantelResponse crear(PlantelRequest request);
    PlantelResponse actualizar(Long id, PlantelRequest request);
    PlantelResponse obtener(Long id);
    List<PlantelResponse> listar();
    List<PlantelResponse> listarPorInstitucion(Long institucionId);
    void desactivar(Long id, Long version);
}
