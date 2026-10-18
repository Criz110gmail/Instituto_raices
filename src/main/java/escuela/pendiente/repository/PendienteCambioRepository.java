package escuela.pendiente.repository;

import escuela.pendiente.entity.PendienteCambio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

public interface PendienteCambioRepository extends JpaRepository<PendienteCambio,Long> {
 Page<PendienteCambio> findByPendienteIdOrderByIdDesc(Long id,Pageable pagina);
}
