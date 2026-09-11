package escuela.academico.service;

import escuela.academico.dto.request.CicloEscolarRequest;
import escuela.academico.dto.response.CicloEscolarResponse;

import java.util.List;

public interface CicloEscolarService {
    CicloEscolarResponse crear(CicloEscolarRequest request);
    CicloEscolarResponse actualizar(Long id, CicloEscolarRequest request);
    CicloEscolarResponse obtener(Long id);
    List<CicloEscolarResponse> listarPorInstitucion(Long institucionId);
}
