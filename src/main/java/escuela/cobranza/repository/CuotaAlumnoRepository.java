package escuela.cobranza.repository;

import escuela.cobranza.entity.CuotaAlumno;
import escuela.cobranza.entity.EstadoCuota;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    @Query(value="SELECT EXISTS(SELECT 1 FROM cargo WHERE cuota_alumno_id=:id)", nativeQuery=true)
    boolean tieneCargos(@Param("id") Long id);

    @Query(value="SELECT id FROM cargo WHERE cuota_alumno_id=:id ORDER BY id DESC LIMIT 1", nativeQuery=true)
    Long ultimoCargo(@Param("id") Long id);

    @Query("select c from CuotaAlumno c where c.id>:ultimoId and c.inscripcion.alumno.institucion.id=:institucionId and (:plantelId is null or c.inscripcion.plantel.id=:plantelId) order by c.id")
    Slice<CuotaAlumno> buscarParaDiagnostico(@Param("institucionId") Long institucionId,
            @Param("plantelId") Long plantelId,@Param("ultimoId") Long ultimoId,Pageable pageable);

    List<CuotaAlumno> findAllByInscripcionIdOrderByCreadoEnDescIdDesc(Long inscripcionId);

    @Query("""
            select c from CuotaAlumno c
            where c.id > :ultimoId
              and c.estado = escuela.cobranza.entity.EstadoCuota.ACTIVA
              and c.generacionAutomatica = true
              and c.conceptoCobro.activo = true
              and c.inscripcion.estado in (
                  escuela.inscripcion.entity.EstadoInscripcion.PREINSCRITA,
                  escuela.inscripcion.entity.EstadoInscripcion.ACTIVA
              )
              and c.inscripcion.alumno.institucion.id = :institucionId
              and (:plantelId is null or c.inscripcion.plantel.id = :plantelId)
              and c.fechaInicio <= :fechaCorte
            order by c.id
            """)
    Slice<CuotaAlumno> buscarParaGeneracion(@Param("institucionId") Long institucionId,
                                             @Param("plantelId") Long plantelId,
                                             @Param("fechaCorte") LocalDate fechaCorte,
                                             @Param("ultimoId") Long ultimoId,
                                             Pageable pageable);
}
