package escuela.academico.repository;

import escuela.academico.entity.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface GradoRepository extends JpaRepository<Grado, Long>, JpaSpecificationExecutor<Grado> {
    List<Grado> findAllByNivelEducativoIdOrderByOrdenAsc(Long nivelEducativoId);
    Optional<Grado> findByNivelEducativoIdAndCodigoIgnoreCase(Long nivelEducativoId, String codigo);
    boolean existsByNivelEducativoIdAndCodigoIgnoreCaseAndIdNot(Long nivelEducativoId, String codigo, Long id);
    boolean existsByNivelEducativoIdAndOrdenAndIdNot(Long nivelEducativoId, int orden, Long id);
}
