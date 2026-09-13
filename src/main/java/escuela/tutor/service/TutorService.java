package escuela.tutor.service;

import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.dto.response.TutorResponse;

import java.util.List;

public interface TutorService {
    TutorResponse crear(TutorRequest request);
    TutorResponse actualizar(Long id, TutorRequest request);
    TutorResponse obtener(Long id);
    List<TutorResponse> listarActivosPorInstitucion(Long institucionId);
    void desactivar(Long id, Long version);
}
