package escuela.inscripcion.repository;

import escuela.inscripcion.entity.AsignacionGrupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AsignacionGrupoRepository extends JpaRepository<AsignacionGrupo, Long> {

    List<AsignacionGrupo> findAllByInscripcionIdOrderByFechaInicioDesc(Long inscripcionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AsignacionGrupo a where a.inscripcion.id = :inscripcionId and a.fechaFin is null")
    Optional<AsignacionGrupo> findAbiertaForUpdate(@Param("inscripcionId") Long inscripcionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AsignacionGrupo a where a.id = :id")
    Optional<AsignacionGrupo> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            select count(a) from AsignacionGrupo a
            where a.grupo.id = :grupoId
              and (a.fechaFin is null or a.fechaFin >= :inicio)
              and (:fin is null or a.fechaInicio <= :fin)
            """)
    long contarSuperpuestas(@Param("grupoId") Long grupoId,
                            @Param("inicio") LocalDate inicio,
                            @Param("fin") LocalDate fin);
}
