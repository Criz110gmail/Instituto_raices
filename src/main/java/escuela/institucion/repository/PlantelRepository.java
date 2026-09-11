package escuela.institucion.repository;

import escuela.institucion.entity.Plantel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PlantelRepository extends JpaRepository<Plantel, Long>, JpaSpecificationExecutor<Plantel> {
    List<Plantel> findAllByOrderByNombreAsc();
    List<Plantel> findAllByInstitucionIdOrderByNombreAsc(Long institucionId);
    Optional<Plantel> findByInstitucionIdAndCodigoIgnoreCase(Long institucionId, String codigo);
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);
}
