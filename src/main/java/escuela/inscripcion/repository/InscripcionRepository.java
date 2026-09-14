package escuela.inscripcion.repository;

import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long>,
        JpaSpecificationExecutor<Inscripcion> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inscripcion i where i.id = :id")
    Optional<Inscripcion> findByIdForUpdate(@Param("id") Long id);

    boolean existsByAlumnoInstitucionIdAndNumeroInscripcionIgnoreCaseAndIdNot(
            Long institucionId, String numeroInscripcion, Long id);

    @Query("""
            select i from Inscripcion i
            where i.alumno.id = :alumnoId and i.id <> :id
              and i.estado in :estados
              and (i.fechaFin is null or i.fechaFin >= :inicio)
              and (:fin is null or i.fechaInicio <= :fin)
            """)
    List<Inscripcion> buscarSuperpuestas(@Param("alumnoId") Long alumnoId,
                                         @Param("id") Long id,
                                         @Param("estados") Collection<EstadoInscripcion> estados,
                                         @Param("inicio") LocalDate inicio,
                                         @Param("fin") LocalDate fin);
}
