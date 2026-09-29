package escuela.docente.repository;

import escuela.docente.entity.Maestro;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface MaestroRepository extends JpaRepository<Maestro, Long>, JpaSpecificationExecutor<Maestro> {
    boolean existsByInstitucionIdAndNumeroEmpleadoIgnoreCaseAndIdNot(Long institucionId, String numero, Long id);
    Optional<Maestro> findByUsuarioId(Long usuarioId);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select m from Maestro m where m.id=:id")
    Optional<Maestro> findByIdForUpdate(@Param("id") Long id);
    @Query("""
        select m from Maestro m where m.institucion.id=:institucionId and m.activo=true
          and (:q='' or lower(concat(m.numeroEmpleado,' ',m.nombres,' ',m.primerApellido,' ',coalesce(m.segundoApellido,''))) like concat('%',:q,'%'))
        order by m.primerApellido,m.segundoApellido,m.nombres,m.id
        """)
    Slice<Maestro> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                             @Param("q") String q, Pageable pageable);
}
