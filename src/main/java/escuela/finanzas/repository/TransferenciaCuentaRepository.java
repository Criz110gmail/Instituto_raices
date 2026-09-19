package escuela.finanzas.repository;

import escuela.finanzas.entity.TransferenciaCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransferenciaCuentaRepository extends JpaRepository<TransferenciaCuenta, Long> {
    Optional<TransferenciaCuenta> findByInstitucionIdAndClaveIdempotencia(Long institucionId,
                                                                           String claveIdempotencia);
}
