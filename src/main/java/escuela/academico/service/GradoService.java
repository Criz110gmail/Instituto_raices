package escuela.academico.service;

import escuela.academico.dto.request.GradoRequest;
import escuela.academico.dto.response.GradoResponse;

import java.util.List;

public interface GradoService {
    GradoResponse crear(GradoRequest request);
    GradoResponse actualizar(Long id, GradoRequest request);
    GradoResponse obtener(Long id);
    List<GradoResponse> listarPorNivel(Long nivelEducativoId);
    void desactivar(Long id, Long version);
}
