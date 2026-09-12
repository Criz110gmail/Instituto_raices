package escuela.seguridad.repository;

import escuela.seguridad.entity.RolPermiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RolPermisoRepository extends JpaRepository<RolPermiso, Long> {
    List<RolPermiso> findAllByRolIdOrderByPermisoCodigoAsc(Long rolId);
    List<RolPermiso> findAllByRolIdAndActivoTrueOrderByPermisoCodigoAsc(Long rolId);
    boolean existsByRolIdAndPermisoId(Long rolId, Long permisoId);
    Optional<RolPermiso> findByRolIdAndPermisoId(Long rolId, Long permisoId);
}
