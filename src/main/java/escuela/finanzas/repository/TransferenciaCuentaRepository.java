package escuela.finanzas.repository;

import escuela.finanzas.entity.TransferenciaCuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface TransferenciaCuentaRepository extends JpaRepository<TransferenciaCuenta, Long> {
    Optional<TransferenciaCuenta> findByInstitucionIdAndClaveIdempotencia(Long institucionId,
                                                                           String claveIdempotencia);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TransferenciaCuenta t where t.id = :id")
    Optional<TransferenciaCuenta> findByIdForUpdate(@Param("id") Long id);
}
