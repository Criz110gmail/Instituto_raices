package escuela.finanzas.repository;

import escuela.finanzas.entity.DevolucionPago;
import escuela.finanzas.entity.EstadoDevolucionPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DevolucionPagoRepository extends JpaRepository<DevolucionPago, Long> {
    boolean existsByPagoIdAndEstado(Long pagoId, EstadoDevolucionPago estado);
    Optional<DevolucionPago> findByInstitucionIdAndClaveIdempotencia(Long institucionId,
                                                                     String claveIdempotencia);
    List<DevolucionPago> findAllByPagoIdAndEstadoOrderByFechaDescIdDesc(Long pagoId,
                                                                        EstadoDevolucionPago estado);
}
