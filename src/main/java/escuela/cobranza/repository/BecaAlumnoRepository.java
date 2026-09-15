package escuela.cobranza.repository;

import escuela.cobranza.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BecaAlumnoRepository extends JpaRepository<BecaAlumno, Long>, JpaSpecificationExecutor<BecaAlumno> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from BecaAlumno b where b.id = :id")
    Optional<BecaAlumno> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select b from BecaAlumno b where b.inscripcion.id = :inscripcionId
              and b.conceptoCobro.id = :conceptoId and b.id <> :id and b.estado = 'ACTIVA'
              and b.fechaFin >= :inicio and b.fechaInicio <= :fin
            """)
    List<BecaAlumno> buscarActivasSuperpuestas(@Param("inscripcionId") Long inscripcionId,
                                                @Param("conceptoId") Long conceptoId,
                                                @Param("id") Long id,
                                                @Param("inicio") LocalDate inicio,
                                                @Param("fin") LocalDate fin);

    @Query("""
            select b from BecaAlumno b where b.inscripcion.id = :inscripcionId
              and b.conceptoCobro.id = :conceptoId and b.estado = 'ACTIVA'
              and b.fechaInicio <= :fin and b.fechaFin >= :inicio
            order by b.id
            """)
    List<BecaAlumno> buscarAplicable(@Param("inscripcionId") Long inscripcionId,
                                      @Param("conceptoId") Long conceptoId,
                                      @Param("inicio") LocalDate inicio,
                                      @Param("fin") LocalDate fin);

    boolean existsByTipoBecaIdAndEstado(Long tipoBecaId, EstadoBeca estado);
    boolean existsByConceptoCobroIdAndEstado(Long conceptoCobroId, EstadoBeca estado);
}
