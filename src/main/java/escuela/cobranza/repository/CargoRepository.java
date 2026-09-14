package escuela.cobranza.repository;

import escuela.cobranza.entity.Cargo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface CargoRepository extends JpaRepository<Cargo, Long>, JpaSpecificationExecutor<Cargo> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cargo c where c.id = :id")
    Optional<Cargo> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query(value = """
            INSERT INTO cargo (
                inscripcion_id, concepto_cobro_id, cuota_alumno_id, clave_generacion,
                descripcion, periodo_cobro_inicio, periodo_cobro_fin, fecha_emision,
                fecha_vencimiento, importe_original, moneda, estado_registro,
                creado_por_id, actualizado_por_id
            ) VALUES (
                :inscripcionId, :conceptoId, :cuotaId, :claveGeneracion,
                :descripcion, :periodoInicio, :periodoFin, :fechaEmision,
                :fechaVencimiento, :importe, :moneda, 'EMITIDO', :actorId, :actorId
            )
            ON CONFLICT (clave_generacion) DO NOTHING
            """, nativeQuery = true)
    int insertarAutomaticoSiAusente(@Param("inscripcionId") Long inscripcionId,
                                     @Param("conceptoId") Long conceptoId,
                                     @Param("cuotaId") Long cuotaId,
                                     @Param("claveGeneracion") String claveGeneracion,
                                     @Param("descripcion") String descripcion,
                                     @Param("periodoInicio") LocalDate periodoInicio,
                                     @Param("periodoFin") LocalDate periodoFin,
                                     @Param("fechaEmision") LocalDate fechaEmision,
                                     @Param("fechaVencimiento") LocalDate fechaVencimiento,
                                     @Param("importe") BigDecimal importe,
                                     @Param("moneda") String moneda,
                                     @Param("actorId") Long actorId);
}
