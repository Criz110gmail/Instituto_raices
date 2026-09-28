package escuela.alumno.repository;

import escuela.alumno.entity.AlumnoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlumnoDocumentoRepository extends JpaRepository<AlumnoDocumento, Long> {
    Optional<AlumnoDocumento> findByIdAndAlumnoId(Long id, Long alumnoId);
    List<AlumnoDocumento> findAllByAlumnoIdOrderByCreadoEnDesc(Long alumnoId);
}

