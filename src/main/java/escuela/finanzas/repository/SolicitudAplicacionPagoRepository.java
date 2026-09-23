package escuela.finanzas.repository;

import escuela.finanzas.entity.SolicitudAplicacionPago;
import escuela.finanzas.dto.response.PagoCargoPendiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolicitudAplicacionPagoRepository extends JpaRepository<SolicitudAplicacionPago, Long> {
    List<SolicitudAplicacionPago> findAllByPagoIdOrderByCargoIdAsc(Long pagoId);

    @Query("select count(s)>0 from SolicitudAplicacionPago s where s.pago.tutor.id=:tutorId and s.pago.estado='PENDIENTE_VALIDACION' and s.cargo.id in :cargoIds")
    boolean existePendiente(@Param("tutorId") Long tutorId, @Param("cargoIds") List<Long> cargoIds);

    @Query(value = """
            SELECT p.id AS pago_id, p.folio, p.fecha_pago, s.monto_solicitado,
                   p.moneda, p.estado,
                   concat_ws(' ', t.nombres, t.primer_apellido, t.segundo_apellido) AS tutor_nombre,
                   p.referencia,
                   (SELECT count(*) FROM comprobante_pago cp WHERE cp.pago_id=p.id) AS comprobantes,
                   p.origen_registro
            FROM solicitud_aplicacion_pago s
            JOIN pago p ON p.id=s.pago_id
            JOIN tutor t ON t.id=p.tutor_id
            WHERE s.cargo_id=:cargoId AND p.estado='PENDIENTE_VALIDACION'
            ORDER BY p.fecha_pago DESC,p.id DESC
            """, nativeQuery = true)
    List<PagoCargoPendiente> pendientesPorCargo(@Param("cargoId") Long cargoId);
}
