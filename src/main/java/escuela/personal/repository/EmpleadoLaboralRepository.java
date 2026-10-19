package escuela.personal.repository;
import escuela.personal.entity.EmpleadoLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EmpleadoLaboralRepository extends JpaRepository<EmpleadoLaboral,Long>{Optional<EmpleadoLaboral> findByEmpleadoId(Long empleadoId);}
