package escuela.cobranza.repository;

import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.EstadoCuota;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CuotaAlumnoRepository extends JpaRepository<CuotaAlumno, Long>,
        JpaSpecificationExecutor<CuotaAlumno> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from CuotaAlumno c where c.id = :id")
    Optional<CuotaAlumno> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select c from CuotaAlumno c
            where c.inscripcion.id = :inscripcionId and c.conceptoCobro.id = :conceptoId
              and c.id <> :id and c.estado = :estado
              and c.fechaFin >= :inicio and c.fechaInicio <= :fin
            """)
    List<CuotaAlumno> buscarSuperpuestas(@Param("inscripcionId") Long inscripcionId,
                                          @Param("conceptoId") Long conceptoId,
                                          @Param("id") Long id,
                                          @Param("estado") EstadoCuota estado,
                                          @Param("inicio") LocalDate inicio,
                                          @Param("fin") LocalDate fin);

    boolean existsByConceptoCobroIdAndEstado(Long conceptoId, EstadoCuota estado);
}
