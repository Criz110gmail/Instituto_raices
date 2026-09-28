package escuela.alumno.service;

import escuela.alumno.dto.request.FichaMedicaAlumnoRequest;
import escuela.alumno.dto.response.FichaMedicaAlumnoResponse;

public interface FichaMedicaAlumnoService {
    FichaMedicaAlumnoResponse obtener(Long alumnoId);
    FichaMedicaAlumnoResponse guardar(Long alumnoId, FichaMedicaAlumnoRequest request);
}

