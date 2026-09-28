package escuela.academico.service;

import escuela.academico.dto.request.MateriaGradoRequest;
import escuela.academico.dto.request.MateriaRequest;
import escuela.academico.dto.response.MateriaGradoResponse;
import escuela.academico.dto.response.MateriaResponse;

import java.util.List;

public interface MateriaService {
    MateriaResponse crear(MateriaRequest request);
    MateriaResponse actualizar(Long id, MateriaRequest request);
    MateriaResponse obtener(Long id);
    void desactivar(Long id, Long version);
    MateriaGradoResponse asignarGrado(Long materiaId, MateriaGradoRequest request);
    MateriaGradoResponse actualizarGrado(Long materiaId, Long planId, MateriaGradoRequest request);
    void desactivarGrado(Long materiaId, Long planId, Long version);
    List<MateriaGradoResponse> listarPlanes(Long materiaId);
}

