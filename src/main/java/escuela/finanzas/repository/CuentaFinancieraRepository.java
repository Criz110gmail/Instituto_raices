package escuela.finanzas.repository;

import escuela.finanzas.entity.CuentaFinanciera;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CuentaFinancieraRepository extends JpaRepository<CuentaFinanciera, Long>,
        JpaSpecificationExecutor<CuentaFinanciera> {

    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CuentaFinanciera c where c.id = :id")
    Optional<CuentaFinanciera> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select c from CuentaFinanciera c
            where c.institucion.id = :institucionId and c.activo = true
              and (c.plantel is null or c.plantel.id = :plantelId)
              and ((:caja = true and c.tipo = 'CAJA') or (:caja = false and c.tipo <> 'CAJA'))
              and (lower(c.codigo) like concat('%', lower(:texto), '%')
                or lower(c.nombre) like concat('%', lower(:texto), '%')
                or lower(coalesce(c.bancoNombre, '')) like concat('%', lower(:texto), '%'))
            order by c.nombre, c.id
            """)
    Slice<CuentaFinanciera> buscarParaPago(@Param("institucionId") Long institucionId,
                                           @Param("plantelId") Long plantelId,
                                           @Param("caja") boolean caja,
                                           @Param("texto") String texto,
                                           Pageable limite);
}
