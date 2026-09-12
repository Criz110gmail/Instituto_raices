package escuela.seguridad.repository;

import escuela.seguridad.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RolRepository extends JpaRepository<Rol, Long>, JpaSpecificationExecutor<Rol> {
    List<Rol> findAllByInstitucionIdOrderByNombreAsc(Long institucionId);
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);
}
