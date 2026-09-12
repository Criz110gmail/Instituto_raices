package escuela.alumno.repository;

import escuela.alumno.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {
    boolean existsByInstitucionIdAndMatriculaIgnoreCaseAndIdNot(Long institucionId, String matricula, Long id);
    boolean existsByInstitucionIdAndCurpIgnoreCaseAndIdNot(Long institucionId, String curp, Long id);
}
