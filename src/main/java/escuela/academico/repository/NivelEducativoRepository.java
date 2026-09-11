package escuela.academico.repository;

import escuela.academico.entity.NivelEducativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface NivelEducativoRepository extends JpaRepository<NivelEducativo, Long>, JpaSpecificationExecutor<NivelEducativo> {
    List<NivelEducativo> findAllByOrderByNombreAsc();
    List<NivelEducativo> findAllByInstitucionIdOrderByOrdenAsc(Long institucionId);
    Optional<NivelEducativo> findByInstitucionIdAndCodigoIgnoreCase(Long institucionId, String codigo);
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);
    boolean existsByInstitucionIdAndOrdenAndIdNot(Long institucionId, int orden, Long id);
}
