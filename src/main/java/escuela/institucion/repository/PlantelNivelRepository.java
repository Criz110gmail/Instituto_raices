package escuela.institucion.repository;

import escuela.institucion.entity.PlantelNivel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PlantelNivelRepository extends JpaRepository<PlantelNivel, Long>, JpaSpecificationExecutor<PlantelNivel> {
    List<PlantelNivel> findAllByPlantelId(Long plantelId);
    Optional<PlantelNivel> findByPlantelIdAndNivelEducativoId(Long plantelId, Long nivelEducativoId);
    boolean existsByPlantelIdAndNivelEducativoIdAndActivoTrue(Long plantelId, Long nivelEducativoId);
    boolean existsByPlantelIdAndNivelEducativoIdAndIdNot(Long plantelId, Long nivelEducativoId, Long id);
}
