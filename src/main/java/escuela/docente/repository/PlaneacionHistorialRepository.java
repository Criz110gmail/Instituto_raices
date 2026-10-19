package escuela.docente.repository;

import escuela.docente.entity.PlaneacionHistorial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import escuela.docente.entity.EstadoPlaneacion;

public interface PlaneacionHistorialRepository extends JpaRepository<PlaneacionHistorial, Long> {
    List<PlaneacionHistorial> findAllByPlaneacionIdOrderByOcurridoEnDescIdDesc(Long planeacionId);
    Optional<PlaneacionHistorial> findFirstByPlaneacionIdAndEstadoNuevoOrderByOcurridoEnDescIdDesc(Long planeacionId, EstadoPlaneacion estado);
}
