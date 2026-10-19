package escuela.personal.repository;
import escuela.personal.entity.Empleado;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface EmpleadoRepository extends JpaRepository<Empleado,Long>,JpaSpecificationExecutor<Empleado> {
 boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId,String codigo,Long id);
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select e from Empleado e where e.id=:id") Optional<Empleado> bloquear(Long id);
}
