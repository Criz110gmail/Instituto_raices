package escuela.tutor.service;

import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.dto.response.TutorResponse;

public interface TutorService {
    TutorResponse crear(TutorRequest request);
    TutorResponse actualizar(Long id, TutorRequest request);
    TutorResponse obtener(Long id);
    void desactivar(Long id, Long version);
}
