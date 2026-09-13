package escuela.tutor.repository;

import escuela.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long>, JpaSpecificationExecutor<Tutor> {
    boolean existsByUsuarioIdAndIdNot(Long usuarioId, Long id);
    List<Tutor> findAllByInstitucionIdAndActivoTrueOrderByPrimerApellidoAscSegundoApellidoAscNombresAsc(
            Long institucionId);
}
