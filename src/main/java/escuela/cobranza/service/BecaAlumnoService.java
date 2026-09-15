package escuela.cobranza.service;
import escuela.cobranza.dto.request.BecaAlumnoRequest;
import escuela.cobranza.dto.response.BecaAlumnoResponse;
public interface BecaAlumnoService {
    BecaAlumnoResponse crear(BecaAlumnoRequest request); BecaAlumnoResponse actualizar(Long id, BecaAlumnoRequest request);
    BecaAlumnoResponse obtener(Long id);
}
