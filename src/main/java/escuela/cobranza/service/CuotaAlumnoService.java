package escuela.cobranza.service;

import escuela.cobranza.dto.request.CuotaAlumnoRequest;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;

public interface CuotaAlumnoService {
    CuotaAlumnoResponse crear(CuotaAlumnoRequest request);
    CuotaAlumnoResponse actualizar(Long id, CuotaAlumnoRequest request);
    CuotaAlumnoResponse obtener(Long id);
}
