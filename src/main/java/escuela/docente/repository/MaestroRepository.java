package escuela.docente.repository;

import escuela.docente.entity.Maestro;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MaestroRepository extends JpaRepository<Maestro, Long>, JpaSpecificationExecutor<Maestro> {
    @Query(value="""
       select exists(select 1 from asignacion_maestro where maestro_id=:id and activo and (fecha_fin is null or fecha_fin>=:hoy))
       or exists(select 1 from horario_clase where maestro_id=:id and activo and fecha_fin>=:hoy)
       or exists(select 1 from planeacion_semanal where maestro_id=:id and estado not in ('PUBLICADA','DESCARTADA'))
       """,nativeQuery=true)
    boolean tieneDependenciasActivas(@Param("id") Long id,@Param("hoy") java.time.LocalDate hoy);
    boolean existsByInstitucionIdAndNumeroEmpleadoIgnoreCaseAndIdNot(Long institucionId, String numero, Long id);
    Optional<Maestro> findByEmpleadoId(Long empleadoId);
    Optional<Maestro> findByUsuarioId(Long usuarioId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select m from Maestro m where m.id=:id")
    Optional<Maestro> findByIdForUpdate(@Param("id") Long id);
    @Query("""
        select m from Maestro m where m.institucion.id=:institucionId and m.activo=true
          and (:q='' or lower(concat(m.numeroEmpleado,' ',m.empleado.nombres,' ',m.empleado.primerApellido,' ',coalesce(m.empleado.segundoApellido,''))) like concat('%',:q,'%'))
        order by m.empleado.primerApellido,m.empleado.segundoApellido,m.empleado.nombres,m.id
        """)
    Slice<Maestro> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                             @Param("q") String q, Pageable pageable);
}
