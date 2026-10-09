package escuela.cobranza.repository;
import escuela.cobranza.entity.AcuerdoAnticipado;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface AcuerdoAnticipadoRepository extends JpaRepository<AcuerdoAnticipado,Long>,JpaSpecificationExecutor<AcuerdoAnticipado> {
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select a from AcuerdoAnticipado a where a.id=:id") Optional<AcuerdoAnticipado> bloquear(@Param("id")Long id);
 @Query(value="SELECT nextval('acuerdo_anticipado_folio_seq')",nativeQuery=true) Long siguienteFolio();
}
