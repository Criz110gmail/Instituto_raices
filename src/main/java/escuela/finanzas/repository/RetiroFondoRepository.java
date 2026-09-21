package escuela.finanzas.repository;

import escuela.finanzas.entity.RetiroFondo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RetiroFondoRepository extends JpaRepository<RetiroFondo, Long>,
        JpaSpecificationExecutor<RetiroFondo> {
    Optional<RetiroFondo> findByInstitucionIdAndClaveIdempotencia(Long institucionId, String claveIdempotencia);
}
