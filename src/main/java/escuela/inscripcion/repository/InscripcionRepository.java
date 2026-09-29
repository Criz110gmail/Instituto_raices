package escuela.inscripcion.repository;

import escuela.inscripcion.entity.EstadoInscripcion;
import escuela.inscripcion.entity.Inscripcion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;

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

    @Query(value = """
            SELECT i.* FROM inscripcion i
            JOIN alumno a ON a.id = i.alumno_id
            WHERE i.plantel_id = :plantelId
              AND i.estado IN ('PREINSCRITA', 'ACTIVA')
              AND (lower(i.numero_inscripcion) LIKE ('%' || lower(:texto) || '%')
                OR a.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%'))
            ORDER BY a.primer_apellido, a.segundo_apellido NULLS LAST, a.nombres, i.id
            """, nativeQuery = true)
    Slice<Inscripcion> buscarParaAutocompletado(@Param("plantelId") Long plantelId,
                                                 @Param("texto") String texto,
                                                 Pageable limite);

    @Query("""
            select (count(i) > 0) from Inscripcion i
            where i.alumno.id = :alumnoId and i.cicloEscolar.id = :cicloId
              and i.estado in (escuela.inscripcion.entity.EstadoInscripcion.PREINSCRITA,
                               escuela.inscripcion.entity.EstadoInscripcion.ACTIVA)
              and (:plantelId is null or i.plantel.id = :plantelId)
            """)
    boolean existeAlumnoEnCicloYPlantel(@Param("alumnoId") Long alumnoId,
                                         @Param("cicloId") Long cicloId,
                                         @Param("plantelId") Long plantelId);

    @Query("""
            select i from Inscripcion i
            where exists (
                select a.id from AsignacionGrupo a
                where a.inscripcion.id = i.id
                  and a.grupo.id = :grupoId
                  and a.fechaInicio <= :finPeriodo
                  and (a.fechaFin is null or a.fechaFin >= :inicioPeriodo)
              )
              and i.fechaInicio <= :finPeriodo
              and (i.fechaFin is null or i.fechaFin >= :inicioPeriodo)
              and i.estado <> escuela.inscripcion.entity.EstadoInscripcion.CANCELADA
            order by i.alumno.primerApellido, i.alumno.segundoApellido, i.alumno.nombres, i.id
            """)
    List<Inscripcion> buscarParaCalificaciones(@Param("grupoId") Long grupoId,
                                               @Param("inicioPeriodo") LocalDate inicioPeriodo,
                                               @Param("finPeriodo") LocalDate finPeriodo);

    @Query("""
            select i from Inscripcion i
            where exists (
                select a.id from AsignacionGrupo a
                where a.inscripcion.id = i.id
                  and a.grupo.id = :grupoId
                  and a.fechaInicio <= :fecha
                  and (a.fechaFin is null or a.fechaFin >= :fecha)
              )
              and i.fechaInicio <= :fecha
              and (i.fechaFin is null or i.fechaFin >= :fecha)
              and i.estado <> escuela.inscripcion.entity.EstadoInscripcion.CANCELADA
            order by i.alumno.primerApellido, i.alumno.segundoApellido, i.alumno.nombres, i.id
            """)
    List<Inscripcion> buscarParaAsistencia(@Param("grupoId") Long grupoId,
                                           @Param("fecha") LocalDate fecha);

    @EntityGraph(attributePaths = {"alumno", "alumno.institucion", "plantel", "cicloEscolar", "grado"})
    @Query("""
            select i from Inscripcion i
            where i.alumno.id = :alumnoId
              and i.alumno.institucion.id = :institucionId
              and i.estado <> escuela.inscripcion.entity.EstadoInscripcion.CANCELADA
              and exists (
                  select c.id from Calificacion c
                  where c.inscripcion.id = i.id
                    and c.estado = escuela.calificacion.entity.EstadoCalificacion.PUBLICADA
                    and c.materiaGrado.incluirBoleta = true
              )
            order by i.cicloEscolar.fechaInicio desc, i.id desc
            """)
    Page<Inscripcion> buscarBoletasPortal(@Param("alumnoId") Long alumnoId,
                                          @Param("institucionId") Long institucionId,
                                          Pageable pagina);

    @EntityGraph(attributePaths = {"alumno", "alumno.institucion", "plantel", "cicloEscolar", "grado"})
    @Query("""
            select i from Inscripcion i
            where i.id = :inscripcionId
              and i.alumno.id = :alumnoId
              and i.alumno.institucion.id = :institucionId
              and i.estado <> escuela.inscripcion.entity.EstadoInscripcion.CANCELADA
              and exists (
                  select c.id from Calificacion c
                  where c.inscripcion.id = i.id
                    and c.estado = escuela.calificacion.entity.EstadoCalificacion.PUBLICADA
                    and c.materiaGrado.incluirBoleta = true
              )
            """)
    Optional<Inscripcion> buscarBoletaPortal(@Param("inscripcionId") Long inscripcionId,
                                             @Param("alumnoId") Long alumnoId,
                                             @Param("institucionId") Long institucionId);
}
