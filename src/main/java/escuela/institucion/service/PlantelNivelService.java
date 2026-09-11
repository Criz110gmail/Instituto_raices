package escuela.institucion.service;

import escuela.institucion.dto.request.PlantelNivelRequest;
import escuela.institucion.dto.response.PlantelNivelResponse;

import java.util.List;

public interface PlantelNivelService {
    PlantelNivelResponse crear(PlantelNivelRequest request);
    PlantelNivelResponse actualizar(Long id, PlantelNivelRequest request);
    PlantelNivelResponse obtener(Long id);
    List<PlantelNivelResponse> listarPorPlantel(Long plantelId);
    void desactivar(Long id, Long version);
}
