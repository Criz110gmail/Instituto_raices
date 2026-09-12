package escuela.seguridad.service;

import escuela.seguridad.dto.request.AsignacionRolRequest;

public interface AdministracionAccesoService {
    Long agregarPermiso(Long rolId, Long permisoId);
    Long asignarRol(AsignacionRolRequest request);
    void desactivarAsignacion(Long usuarioRolId, Long version);
}
