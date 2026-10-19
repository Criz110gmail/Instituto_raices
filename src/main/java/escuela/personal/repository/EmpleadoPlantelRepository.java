package escuela.personal.repository;
import escuela.personal.entity.EmpleadoPlantel;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import java.util.Optional;
public interface EmpleadoPlantelRepository extends JpaRepository<EmpleadoPlantel,Long>,JpaSpecificationExecutor<EmpleadoPlantel>{
 Page<EmpleadoPlantel> findByEmpleadoId(Long empleadoId,Pageable pagina);
 Optional<EmpleadoPlantel> findByIdAndEmpleadoId(Long id,Long empleadoId);
}
