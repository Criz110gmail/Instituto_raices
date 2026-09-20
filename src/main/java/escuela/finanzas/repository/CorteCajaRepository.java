package escuela.finanzas.repository;

import escuela.finanzas.entity.CorteCaja;
import escuela.finanzas.entity.EstadoCorteCaja;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CorteCajaRepository extends JpaRepository<CorteCaja, Long>,
        JpaSpecificationExecutor<CorteCaja> {
    Optional<CorteCaja> findByCuentaIdAndEstado(Long cuentaId, EstadoCorteCaja estado);
    Optional<CorteCaja> findByInstitucionIdAndClaveApertura(Long institucionId, String claveApertura);
    Optional<CorteCaja> findByInstitucionIdAndClaveCierre(Long institucionId, String claveCierre);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CorteCaja c where c.id = :id")
    Optional<CorteCaja> findByIdForUpdate(@Param("id") Long id);
}
