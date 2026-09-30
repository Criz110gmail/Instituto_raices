package escuela.horario.repository;

import escuela.horario.entity.HorarioClase;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface HorarioClaseRepository extends JpaRepository<HorarioClase,Long>,JpaSpecificationExecutor<HorarioClase>{
    @Query("""
      select count(h)>0 from HorarioClase h where h.id<>:id and h.activo=true and h.diaSemana=:dia
      and h.fechaInicio<=:fin and h.fechaFin>=:inicio and h.horaInicio<:horaFin and h.horaFin>:horaInicio
      and (h.maestro.id=:maestroId or h.grupo.id=:grupoId or
          (:aula is not null and h.plantel.id=:plantelId and lower(h.aula)=lower(:aula)))
      """)
    boolean existeCruce(@Param("id")Long id,@Param("dia")int dia,@Param("inicio")LocalDate inicio,
        @Param("fin")LocalDate fin,@Param("horaInicio")LocalTime horaInicio,@Param("horaFin")LocalTime horaFin,
        @Param("maestroId")Long maestroId,@Param("grupoId")Long grupoId,@Param("plantelId")Long plantelId,
        @Param("aula")String aula);

    @Query("""
      select h from HorarioClase h join fetch h.grupo g join fetch g.grado join fetch h.materia
      join fetch h.plantel where h.maestro.id=:maestroId and h.activo=true
      and h.fechaInicio<=:fecha and h.fechaFin>=:fecha order by h.diaSemana,h.horaInicio,h.id
      """)
    List<HorarioClase> horarioMaestro(@Param("maestroId")Long maestroId,@Param("fecha")LocalDate fecha);

    @Query("""
      select h from HorarioClase h join fetch h.maestro join fetch h.grupo g join fetch g.grado
      join fetch h.materia join fetch h.plantel where h.activo=true and h.fechaInicio<=:fecha and h.fechaFin>=:fecha
      and exists (select ag.id from AsignacionGrupo ag join ag.inscripcion i
        where ag.grupo.id=h.grupo.id and i.alumno.id=:alumnoId
        and i.estado=escuela.inscripcion.entity.EstadoInscripcion.ACTIVA
        and ag.fechaInicio<=:fecha and (ag.fechaFin is null or ag.fechaFin>=:fecha))
      order by h.diaSemana,h.horaInicio,h.id
      """)
    List<HorarioClase> horarioAlumno(@Param("alumnoId")Long alumnoId,@Param("fecha")LocalDate fecha);
}
