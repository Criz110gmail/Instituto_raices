package escuela.pendiente.repository;

import escuela.pendiente.entity.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;
import java.time.LocalDate;

public interface PendienteRepository extends JpaRepository<PendientePersonal,Long>,JpaSpecificationExecutor<PendientePersonal> {
 Optional<PendientePersonal> findByIdAndUsuarioIdAndUsuarioInstitucionId(Long id,Long usuarioId,Long institucionId);
 long countByUsuarioIdAndEstadoInAndFechaCompromisoBetween(Long usuarioId,Collection<EstadoPendiente> estados,LocalDate desde,LocalDate hasta);
 long countByUsuarioIdAndEstadoInAndFechaCompromisoBefore(Long usuarioId,Collection<EstadoPendiente> estados,LocalDate hasta);
}
