package escuela.docente.repository;

import escuela.docente.entity.PlaneacionHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlaneacionHistorialRepository extends JpaRepository<PlaneacionHistorial, Long> {
    List<PlaneacionHistorial> findAllByPlaneacionIdOrderByOcurridoEnDescIdDesc(Long planeacionId);
}
