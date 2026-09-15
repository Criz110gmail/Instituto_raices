package escuela.cobranza.repository;

import escuela.cobranza.entity.AjusteCargo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface AjusteCargoRepository extends JpaRepository<AjusteCargo, Long>, JpaSpecificationExecutor<AjusteCargo> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AjusteCargo a where a.id = :id")
    Optional<AjusteCargo> findByIdForUpdate(@Param("id") Long id);
    boolean existsByReversaDeId(Long ajusteId);
    boolean existsByCargoIdAndBecaAlumnoIdAndReversaDeIsNull(Long cargoId, Long becaId);
}
