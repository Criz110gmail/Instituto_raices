package escuela.tutor.repository;

import escuela.tutor.entity.TutorIdentificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TutorIdentificacionRepository extends JpaRepository<TutorIdentificacion, Long> {
    Optional<TutorIdentificacion> findFirstByTutorIdAndRetiradaEnIsNull(Long tutorId);
    Optional<TutorIdentificacion> findByIdAndTutorId(Long id, Long tutorId);
    List<TutorIdentificacion> findAllByTutorIdOrderByCreadoEnDesc(Long tutorId);
}
