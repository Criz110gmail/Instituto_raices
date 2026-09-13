package escuela.alumno.repository;

import escuela.alumno.entity.AlumnoFotografia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlumnoFotografiaRepository extends JpaRepository<AlumnoFotografia, Long> {
    Optional<AlumnoFotografia> findFirstByAlumnoIdAndRetiradaEnIsNull(Long alumnoId);
    Optional<AlumnoFotografia> findByIdAndAlumnoId(Long id, Long alumnoId);
    List<AlumnoFotografia> findAllByAlumnoIdOrderByCreadoEnDesc(Long alumnoId);
}
