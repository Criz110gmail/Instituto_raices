package escuela.finanzas.repository;

import escuela.finanzas.entity.ComprobantePago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ComprobantePagoRepository extends JpaRepository<ComprobantePago, Long> {
    Optional<ComprobantePago> findByIdAndPagoId(Long id, Long pagoId);
}
