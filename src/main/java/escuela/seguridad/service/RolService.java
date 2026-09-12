package escuela.seguridad.service;

import escuela.seguridad.dto.request.RolRequest;
import escuela.seguridad.dto.response.RolResponse;
import escuela.seguridad.dto.response.PermisoResponse;

import java.util.List;
import java.util.Set;

public interface RolService {
    RolResponse crear(RolRequest request, Set<Long> permisoIds);
    RolResponse actualizar(Long id, RolRequest request, Set<Long> permisoIds);
    RolResponse obtener(Long id);
    List<PermisoResponse> listarPermisos();
    List<RolResponse> listarRoles();
    Set<Long> permisosAsignados(Long rolId);
    void desactivar(Long id, Long version);
}
