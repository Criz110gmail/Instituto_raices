package escuela.cobranza.repository;
import escuela.cobranza.entity.AcuerdoAnticipadoCargo;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AcuerdoAnticipadoCargoRepository extends JpaRepository<AcuerdoAnticipadoCargo,Long> {
 boolean existsByCargoIdAndActivoTrue(Long id);
}
