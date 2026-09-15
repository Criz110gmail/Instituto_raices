package escuela.finanzas.repository;

import escuela.finanzas.entity.MovimientoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovimientoFinancieroRepository extends JpaRepository<MovimientoFinanciero, Long> {
    Optional<MovimientoFinanciero> findByPagoId(Long pagoId);
    boolean existsByCuentaId(Long cuentaId);

    Optional<MovimientoFinanciero> findFirstByCuentaIdOrderBySecuenciaCuentaDesc(Long cuentaId);
}
