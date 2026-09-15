package escuela.finanzas.repository;

import escuela.finanzas.entity.AplicacionPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AplicacionPagoRepository extends JpaRepository<AplicacionPago, Long> {
    List<AplicacionPago> findAllByPagoIdOrderByIdAsc(Long pagoId);
}
