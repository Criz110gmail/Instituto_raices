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
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Query("""
            select a from AsignacionGrupo a
            where a.inscripcion.id = :inscripcionId
              and a.fechaInicio <= :fin
              and (a.fechaFin is null or a.fechaFin >= :inicio)
            order by a.fechaInicio desc, a.id desc
            """)
    List<AsignacionGrupo> buscarParaPeriodo(@Param("inscripcionId") Long inscripcionId,
                                            @Param("inicio") LocalDate inicio,
                                            @Param("fin") LocalDate fin);

    @Query("""
            select a from AsignacionGrupo a
            join fetch a.grupo g
            where a.inscripcion.id in :inscripcionIds
            order by a.inscripcion.id, a.fechaInicio desc, a.id desc
            """)
    List<AsignacionGrupo> buscarHistorialParaBoleta(
            @Param("inscripcionIds") Collection<Long> inscripcionIds);

    @Query(value="""
            select distinct ag from AsignacionGrupo ag
            join fetch ag.inscripcion i join fetch i.alumno al join fetch ag.grupo g
            join fetch g.plantel join fetch g.grado
            where i.estado=escuela.inscripcion.entity.EstadoInscripcion.ACTIVA
              and ag.fechaInicio<=:fecha and (ag.fechaFin is null or ag.fechaFin>=:fecha)
              and exists (select am.id from AsignacionMaestro am where am.maestro.id=:maestroId
                and am.grupo.id=g.id and am.activo=true and am.fechaInicio<=:fecha
                and (am.fechaFin is null or am.fechaFin>=:fecha))
              and (:q='' or lower(concat(al.matricula,' ',al.nombres,' ',al.primerApellido,' ',coalesce(al.segundoApellido,''))) like concat('%',:q,'%'))
            """, countQuery="""
            select count(distinct ag.id) from AsignacionGrupo ag join ag.inscripcion i join i.alumno al join ag.grupo g
            where i.estado=escuela.inscripcion.entity.EstadoInscripcion.ACTIVA
              and ag.fechaInicio<=:fecha and (ag.fechaFin is null or ag.fechaFin>=:fecha)
              and exists (select am.id from AsignacionMaestro am where am.maestro.id=:maestroId
                and am.grupo.id=g.id and am.activo=true and am.fechaInicio<=:fecha
                and (am.fechaFin is null or am.fechaFin>=:fecha))
              and (:q='' or lower(concat(al.matricula,' ',al.nombres,' ',al.primerApellido,' ',coalesce(al.segundoApellido,''))) like concat('%',:q,'%'))
            """)
    Page<AsignacionGrupo> alumnosDelMaestro(@Param("maestroId")Long maestroId,@Param("fecha")LocalDate fecha,
                                             @Param("q")String q,Pageable pageable);

    @Query("""
            select ag from AsignacionGrupo ag join fetch ag.inscripcion i join fetch i.alumno al join fetch ag.grupo g
            join fetch g.plantel join fetch g.grado
            where al.id=:alumnoId and i.estado=escuela.inscripcion.entity.EstadoInscripcion.ACTIVA
              and ag.fechaInicio<=:fecha and (ag.fechaFin is null or ag.fechaFin>=:fecha)
              and exists (select am.id from AsignacionMaestro am where am.maestro.id=:maestroId
                and am.grupo.id=g.id and am.activo=true and am.fechaInicio<=:fecha
                and (am.fechaFin is null or am.fechaFin>=:fecha))
            order by ag.fechaInicio desc
            """)
    List<AsignacionGrupo> alumnoDelMaestro(@Param("maestroId")Long maestroId,@Param("alumnoId")Long alumnoId,@Param("fecha")LocalDate fecha);
}
