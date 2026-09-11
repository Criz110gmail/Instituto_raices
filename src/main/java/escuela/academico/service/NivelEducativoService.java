package escuela.academico.service;

import escuela.academico.dto.request.NivelEducativoRequest;
import escuela.academico.dto.response.NivelEducativoResponse;

import java.util.List;

public interface NivelEducativoService {
    NivelEducativoResponse crear(NivelEducativoRequest request);
    NivelEducativoResponse actualizar(Long id, NivelEducativoRequest request);
    NivelEducativoResponse obtener(Long id);
    List<NivelEducativoResponse> listar();
    List<NivelEducativoResponse> listarPorInstitucion(Long institucionId);
    void desactivar(Long id, Long version);
}
