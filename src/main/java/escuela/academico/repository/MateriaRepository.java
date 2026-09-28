package escuela.academico.repository;

import escuela.academico.entity.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MateriaRepository extends JpaRepository<Materia, Long>, JpaSpecificationExecutor<Materia> {
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);
}

