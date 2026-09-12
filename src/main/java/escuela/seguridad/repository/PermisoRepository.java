package escuela.seguridad.repository;

import escuela.seguridad.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    List<Permiso> findAllByOrderByCodigoAsc();
    Optional<Permiso> findByCodigoIgnoreCase(String codigo);
}
