package escuela.finanzas.repository;

import escuela.finanzas.entity.ReversionFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReversionFinancieraRepository extends JpaRepository<ReversionFinanciera, Long> {
    Optional<ReversionFinanciera> findByInstitucionIdAndClaveIdempotencia(Long institucionId,
                                                                          String claveIdempotencia);
}
