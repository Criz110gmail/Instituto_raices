package escuela.cobranza.repository;
import escuela.cobranza.entity.PoliticaRecargo;
import org.springframework.data.jpa.repository.*;import org.springframework.data.repository.query.Param;
import java.util.Optional;
public interface PoliticaRecargoRepository extends JpaRepository<PoliticaRecargo,Long>,JpaSpecificationExecutor<PoliticaRecargo>{
 boolean existsByConceptoCobroIdAndIdNot(Long conceptoId,Long id);
 boolean existsByConceptoCobroIdAndActivoTrue(Long conceptoId);
 Optional<PoliticaRecargo> findByConceptoCobroIdAndActivoTrueAndGeneracionAutomaticaTrue(Long conceptoId);
 @Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE) @Query("select p from PoliticaRecargo p where p.id=:id") Optional<PoliticaRecargo> findByIdForUpdate(@Param("id")Long id);
}
