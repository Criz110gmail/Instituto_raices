package escuela.alumno.service;

import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.dto.response.AlumnoResponse;

import java.util.List;

public interface AlumnoService {
    AlumnoResponse crear(AlumnoRequest request);
    AlumnoResponse actualizar(Long id, AlumnoRequest request);
    AlumnoResponse obtener(Long id);
    List<AlumnoResponse> listarActivosPorInstitucion(Long institucionId);
    void desactivar(Long id, Long version);
}
