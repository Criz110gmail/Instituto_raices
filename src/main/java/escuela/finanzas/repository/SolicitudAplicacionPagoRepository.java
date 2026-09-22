package escuela.finanzas.repository;

import escuela.finanzas.entity.SolicitudAplicacionPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitudAplicacionPagoRepository extends JpaRepository<SolicitudAplicacionPago, Long> {
    List<SolicitudAplicacionPago> findAllByPagoIdOrderByCargoIdAsc(Long pagoId);

    @Query("select count(s)>0 from SolicitudAplicacionPago s where s.pago.tutor.id=:tutorId and s.pago.estado='PENDIENTE_VALIDACION' and s.cargo.id in :cargoIds")
    boolean existePendiente(@Param("tutorId") Long tutorId, @Param("cargoIds") List<Long> cargoIds);
}
