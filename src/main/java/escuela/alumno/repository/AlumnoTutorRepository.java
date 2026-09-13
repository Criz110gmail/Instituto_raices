package escuela.alumno.repository;

import escuela.alumno.entity.AlumnoTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AlumnoTutorRepository extends JpaRepository<AlumnoTutor, Long>,
        JpaSpecificationExecutor<AlumnoTutor> {

    List<AlumnoTutor> findAllByAlumnoIdAndTutorIdAndActivoTrueAndIdNot(
            Long alumnoId, Long tutorId, Long id);

    List<AlumnoTutor> findAllByAlumnoIdAndContactoPrincipalTrueAndActivoTrueAndIdNot(
            Long alumnoId, Long id);
}
