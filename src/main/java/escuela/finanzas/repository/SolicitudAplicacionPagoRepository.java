package escuela.finanzas.repository;

import escuela.finanzas.entity.SolicitudAplicacionPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudAplicacionPagoRepository extends JpaRepository<SolicitudAplicacionPago, Long> {
    List<SolicitudAplicacionPago> findAllByPagoIdOrderByCargoIdAsc(Long pagoId);
}
