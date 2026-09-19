package escuela.finanzas.repository;

import escuela.finanzas.entity.MovimientoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

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
    Optional<MovimientoFinanciero> findByDevolucionPagoId(Long devolucionPagoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from MovimientoFinanciero m where m.id = :id")
    Optional<MovimientoFinanciero> findByIdForUpdate(@Param("id") Long id);
}
