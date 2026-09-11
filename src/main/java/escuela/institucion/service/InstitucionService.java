package escuela.institucion.service;

import escuela.institucion.dto.request.InstitucionRequest;
import escuela.institucion.dto.response.InstitucionResponse;

import java.util.List;

public interface InstitucionService {
    InstitucionResponse crear(InstitucionRequest request);
    InstitucionResponse actualizar(Long id, InstitucionRequest request);
    InstitucionResponse obtener(Long id);
    List<InstitucionResponse> listar();
    void desactivar(Long id, Long version);
}
