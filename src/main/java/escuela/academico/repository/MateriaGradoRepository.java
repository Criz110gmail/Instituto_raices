package escuela.academico.repository;

import escuela.academico.entity.MateriaGrado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MateriaGradoRepository extends JpaRepository<MateriaGrado, Long> {
    List<MateriaGrado> findAllByMateriaIdOrderByActivoDescOrdenAsc(Long materiaId);
    Optional<MateriaGrado> findByIdAndMateriaId(Long id, Long materiaId);
    boolean existsByMateriaIdAndGradoId(Long materiaId, Long gradoId);
    boolean existsByGradoIdAndOrdenAndActivoTrueAndIdNot(Long gradoId, Integer orden, Long id);
    long countByMateriaIdAndActivoTrue(Long materiaId);
}
