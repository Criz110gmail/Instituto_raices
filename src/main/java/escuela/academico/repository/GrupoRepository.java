package escuela.academico.repository;

import escuela.academico.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GrupoRepository extends JpaRepository<Grupo, Long>, JpaSpecificationExecutor<Grupo> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Grupo g where g.id = :id")
    Optional<Grupo> findByIdForUpdate(@Param("id") Long id);
    List<Grupo> findAllByPlantelIdAndCicloEscolarIdOrderByNombreAsc(Long plantelId, Long cicloEscolarId);
    boolean existsByPlantelIdAndCicloEscolarIdAndGradoIdAndTurnoAndNombreIgnoreCaseAndIdNot(
            Long plantelId, Long cicloEscolarId, Long gradoId, escuela.academico.entity.Turno turno,
            String nombre, Long id);
    boolean existsByPlantelIdAndCicloEscolarIdAndCodigoIgnoreCaseAndIdNot(
            Long plantelId, Long cicloEscolarId, String codigo, Long id);
}
