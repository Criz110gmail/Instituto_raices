package escuela.cobranza.repository;
import escuela.cobranza.entity.ConvenioPago;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface ConvenioPagoRepository extends JpaRepository<ConvenioPago,Long>,JpaSpecificationExecutor<ConvenioPago>{
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select c from ConvenioPago c where c.id=:id") Optional<ConvenioPago> findByIdForUpdate(@Param("id")Long id);
 @Query(value="select nextval('seq_convenio_pago_folio')",nativeQuery=true) long siguienteFolio();
}
