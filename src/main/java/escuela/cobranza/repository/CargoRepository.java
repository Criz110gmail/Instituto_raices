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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface CargoRepository extends JpaRepository<Cargo, Long>, JpaSpecificationExecutor<Cargo> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cargo c where c.id = :id")
    Optional<Cargo> findByIdForUpdate(@Param("id") Long id);

    Optional<Cargo> findByClaveGeneracion(String claveGeneracion);

    @Query("""
            select c from Cargo c join c.conceptoCobro concepto join PoliticaRecargo p
              on p.conceptoCobro.id = concepto.id
            where c.id > :ultimoId and c.estadoRegistro = 'EMITIDO'
              and p.activo = true and p.generacionAutomatica = true
              and concepto.activo = true and concepto.permiteRecargo = true
              and c.fechaVencimiento <= :fechaCorte
              and c.inscripcion.alumno.institucion.id = :institucionId
              and (:plantelId is null or c.inscripcion.plantel.id = :plantelId)
            order by c.id
            """)
    Slice<Cargo> buscarParaRecargo(@Param("institucionId")Long institucionId,
                                    @Param("plantelId")Long plantelId,
                                    @Param("fechaCorte")LocalDate fechaCorte,
                                    @Param("ultimoId")Long ultimoId,Pageable limite);

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
