package escuela.academico.repository;

import escuela.academico.entity.CicloEscolar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CicloEscolarRepository extends JpaRepository<CicloEscolar, Long>, JpaSpecificationExecutor<CicloEscolar> {
    List<CicloEscolar> findAllByInstitucionIdOrderByFechaInicioDesc(Long institucionId);
    Optional<CicloEscolar> findByInstitucionIdAndCodigoIgnoreCase(Long institucionId, String codigo);
    Optional<CicloEscolar> findByInstitucionIdAndPredeterminadoTrue(Long institucionId);
    Optional<CicloEscolar> findByInstitucionIdAndPredeterminadoTrueAndIdNot(Long institucionId, Long id);
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);
}
