package escuela.inscripcion.service;

import escuela.inscripcion.dto.request.AsignacionGrupoRequest;
import escuela.inscripcion.dto.request.InscripcionRequest;
import escuela.inscripcion.dto.response.AsignacionGrupoResponse;
import escuela.inscripcion.dto.response.InscripcionResponse;

import java.time.LocalDate;
import java.util.List;

public interface InscripcionService {
    InscripcionResponse crear(InscripcionRequest request);
    InscripcionResponse actualizar(Long id, InscripcionRequest request);
    InscripcionResponse obtener(Long id);
    AsignacionGrupoResponse asignarGrupo(Long inscripcionId, AsignacionGrupoRequest request);
    void finalizarAsignacion(Long inscripcionId, Long asignacionId, Long version,
                             LocalDate fechaFin);
    List<AsignacionGrupoResponse> listarAsignaciones(Long inscripcionId);
}
