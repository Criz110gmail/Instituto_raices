package escuela.docente.repository;

import escuela.docente.entity.PlaneacionVersionPublicada;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface PlaneacionVersionPublicadaRepository extends JpaRepository<PlaneacionVersionPublicada, Long> {
    List<PlaneacionVersionPublicada> findAllByPlaneacionIdOrderByNumeroRevisionDesc(Long planeacionId);
    Optional<PlaneacionVersionPublicada> findByPlaneacionIdAndNumeroRevision(Long planeacionId, int numeroRevision);
}
