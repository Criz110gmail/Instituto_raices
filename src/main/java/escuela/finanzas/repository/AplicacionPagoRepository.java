package escuela.finanzas.repository;

import escuela.finanzas.entity.AplicacionPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

public interface AplicacionPagoRepository extends JpaRepository<AplicacionPago, Long> {
    java.util.Optional<AplicacionPago> findByPagoIdAndClaveSaldoFavor(Long pagoId,String claveSaldoFavor);
    @Query("select a from AplicacionPago a where a.pago.id=:pagoId and a.saldoFavor=true and (:operacion='' or cast(a.operacion as string)=:operacion) order by a.id desc")
    org.springframework.data.domain.Page<AplicacionPago> historialSaldo(@Param("pagoId") Long pagoId,@Param("operacion")String operacion,org.springframework.data.domain.Pageable pagina);
    List<AplicacionPago> findAllByPagoIdOrderByIdAsc(Long pagoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select a from AplicacionPago a
            where a.pago.id = :pagoId and a.operacion = 'APLICAR'
              and not exists (select r.id from AplicacionPago r where r.reversaDe = a)
            order by a.id
            """)
    List<AplicacionPago> findAplicacionesActivasByPagoIdForUpdate(@Param("pagoId") Long pagoId);
}
