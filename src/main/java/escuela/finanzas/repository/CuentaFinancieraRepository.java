package escuela.finanzas.repository;

import escuela.finanzas.entity.CuentaFinanciera;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CuentaFinancieraRepository extends JpaRepository<CuentaFinanciera, Long>,
        JpaSpecificationExecutor<CuentaFinanciera> {

    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CuentaFinanciera c where c.id = :id")
    Optional<CuentaFinanciera> findByIdForUpdate(@Param("id") Long id);
}
