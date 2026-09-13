package escuela.alumno.service;

import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.dto.response.AlumnoResponse;

public interface AlumnoService {
    AlumnoResponse crear(AlumnoRequest request);
    AlumnoResponse actualizar(Long id, AlumnoRequest request);
    AlumnoResponse obtener(Long id);
    void desactivar(Long id, Long version);
}
