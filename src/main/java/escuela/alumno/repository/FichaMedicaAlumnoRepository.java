package escuela.alumno.repository;

import escuela.alumno.entity.FichaMedicaAlumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FichaMedicaAlumnoRepository extends JpaRepository<FichaMedicaAlumno, Long> {
    Optional<FichaMedicaAlumno> findByAlumnoId(Long alumnoId);
}

