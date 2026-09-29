package escuela.docente.repository;

import escuela.docente.entity.AsignacionMaestro;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface AsignacionMaestroRepository extends JpaRepository<AsignacionMaestro, Long> {
    List<AsignacionMaestro> findAllByMaestroIdOrderByActivoDescFechaInicioDescIdDesc(Long maestroId);
    Optional<AsignacionMaestro> findByIdAndMaestroId(Long id, Long maestroId);
    @Query("""
        select count(a)>0 from AsignacionMaestro a
        where a.maestro.id=:maestroId and a.grupo.id=:grupoId and a.materia.id=:materiaId and a.id<>:id
          and a.activo=true and a.fechaInicio<=coalesce(:fechaFin,:fechaInicio)
          and (a.fechaFin is null or a.fechaFin>=:fechaInicio)
        """)
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
