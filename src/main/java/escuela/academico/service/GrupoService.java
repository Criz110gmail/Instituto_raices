package escuela.academico.service;

import escuela.academico.dto.request.GrupoRequest;
import escuela.academico.dto.response.GrupoResponse;

import java.util.List;

public interface GrupoService {
    GrupoResponse crear(GrupoRequest request);
    GrupoResponse actualizar(Long id, GrupoRequest request);
    GrupoResponse obtener(Long id);
    List<GrupoResponse> listarPorPlantelYCiclo(Long plantelId, Long cicloEscolarId);
    void desactivar(Long id, Long version);
}
