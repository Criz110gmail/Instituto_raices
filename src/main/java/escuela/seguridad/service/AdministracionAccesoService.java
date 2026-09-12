package escuela.seguridad.service;

import escuela.seguridad.dto.request.AsignacionRolRequest;
import escuela.seguridad.dto.response.AsignacionRolResponse;

import java.util.List;

public interface AdministracionAccesoService {
    Long agregarPermiso(Long rolId, Long permisoId);
    Long asignarRol(AsignacionRolRequest request);
    List<AsignacionRolResponse> listarAsignaciones(Long usuarioId);
    void desactivarAsignacion(Long usuarioId, Long usuarioRolId, Long version);
}
