package escuela.academico.repository;

import escuela.academico.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GrupoRepository extends JpaRepository<Grupo, Long>, JpaSpecificationExecutor<Grupo> {
    List<Grupo> findAllByPlantelIdAndCicloEscolarIdOrderByNombreAsc(Long plantelId, Long cicloEscolarId);
    boolean existsByPlantelIdAndCicloEscolarIdAndGradoIdAndTurnoAndNombreIgnoreCaseAndIdNot(
            Long plantelId, Long cicloEscolarId, Long gradoId, escuela.academico.entity.Turno turno,
            String nombre, Long id);
    boolean existsByPlantelIdAndCicloEscolarIdAndCodigoIgnoreCaseAndIdNot(
            Long plantelId, Long cicloEscolarId, String codigo, Long id);
}
