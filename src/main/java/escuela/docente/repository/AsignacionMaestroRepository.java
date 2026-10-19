package escuela.docente.repository;

import escuela.docente.entity.AsignacionMaestro;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface AsignacionMaestroRepository extends JpaRepository<AsignacionMaestro, Long>, JpaSpecificationExecutor<AsignacionMaestro> {
    List<AsignacionMaestro> findAllByMaestroIdOrderByActivoDescFechaInicioDescIdDesc(Long maestroId);
    Optional<AsignacionMaestro> findByIdAndMaestroId(Long id, Long maestroId);
    // Tipar explícitamente las fechas nulas evita date <= text en PostgreSQL.
    // Sin fecha final significa vigencia abierta, no solamente el día inicial.
    @Query(value="""
        select count(*)>0 from asignacion_maestro a
        where a.maestro_id=:maestroId and a.grupo_id=:grupoId and a.materia_id=:materiaId and a.id<>:id
          and a.activo=true
          and (cast(:fechaFin as date) is null or a.fecha_inicio<=cast(:fechaFin as date))
          and (a.fecha_fin is null or a.fecha_fin>=cast(:fechaInicio as date))
        """,nativeQuery=true)
    boolean existeTraslape(@Param("maestroId") Long maestroId, @Param("grupoId") Long grupoId,
                            @Param("materiaId") Long materiaId, @Param("fechaInicio") LocalDate fechaInicio,
                            @Param("fechaFin") LocalDate fechaFin, @Param("id") Long id);
    @Query("""
        select a from AsignacionMaestro a join fetch a.grupo g join fetch g.plantel join fetch g.grado
          join fetch g.cicloEscolar join fetch a.materia
        where a.maestro.id=:maestroId and a.activo=true and a.fechaInicio<=:fechaFin
          and (a.fechaFin is null or a.fechaFin>=:fechaInicio)
        order by g.plantel.nombre,g.nombre,a.materia.nombre
        """)
    List<AsignacionMaestro> vigentes(@Param("maestroId") Long maestroId,
                                     @Param("fechaInicio") LocalDate fechaInicio,
                                     @Param("fechaFin") LocalDate fechaFin);
    @Query("""
        select a from AsignacionMaestro a join fetch a.materia
        where a.maestro.id=:maestroId and a.grupo.id=:grupoId and a.activo=true
          and a.fechaInicio<=:fechaFin and (a.fechaFin is null or a.fechaFin>=:fechaInicio)
        order by a.materia.nombre,a.id
        """)
    List<AsignacionMaestro> materiasVigentes(@Param("maestroId") Long maestroId,
                                              @Param("grupoId") Long grupoId,
                                              @Param("fechaInicio") LocalDate fechaInicio,
                                              @Param("fechaFin") LocalDate fechaFin);
}
