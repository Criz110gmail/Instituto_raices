package escuela.finanzas.repository;

import escuela.finanzas.entity.MovimientoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.List;

public interface MovimientoFinancieroRepository extends JpaRepository<MovimientoFinanciero, Long>,
        JpaSpecificationExecutor<MovimientoFinanciero> {
    Optional<MovimientoFinanciero> findByPagoId(Long pagoId);
    Optional<MovimientoFinanciero> findByInstitucionIdAndClaveIdempotencia(Long institucionId,
                                                                            String claveIdempotencia);
    boolean existsByCuentaId(Long cuentaId);

    Optional<MovimientoFinanciero> findFirstByCuentaIdOrderBySecuenciaCuentaDesc(Long cuentaId);
    List<MovimientoFinanciero> findAllByTransferenciaIdOrderByDireccionDesc(Long transferenciaId);
}
