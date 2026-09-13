package escuela.alumno.service;

import escuela.alumno.dto.request.AlumnoTutorRequest;
import escuela.alumno.dto.response.AlumnoTutorResponse;

public interface AlumnoTutorService {
    AlumnoTutorResponse crear(AlumnoTutorRequest request);
    AlumnoTutorResponse actualizar(Long id, AlumnoTutorRequest request);
    AlumnoTutorResponse obtener(Long id);
    void desactivar(Long id, Long version);
}
