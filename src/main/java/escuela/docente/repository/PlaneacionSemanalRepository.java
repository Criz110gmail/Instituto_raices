package escuela.docente.repository;

import escuela.docente.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.*;

public interface PlaneacionSemanalRepository extends JpaRepository<PlaneacionSemanal, Long>, JpaSpecificationExecutor<PlaneacionSemanal> {
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from PlaneacionSemanal p where p.id=:id")
    Optional<PlaneacionSemanal> findByIdForUpdate(@Param("id") Long id);
    @Query("select p from PlaneacionSemanal p where p.id=:id")
    Optional<PlaneacionSemanal> findDetalleById(@Param("id") Long id);
    @Query("""
        select count(p)>0 from PlaneacionSemanal p
        where p.maestro.id=:maestroId and p.grupo.id=:grupoId and p.id<>:id
          and p.estado<>escuela.docente.entity.EstadoPlaneacion.DESCARTADA
          and p.fechaInicio<=:fechaFin and p.fechaFin>=:fechaInicio
        """)
    boolean existeTraslape(@Param("maestroId") Long maestroId, @Param("grupoId") Long grupoId,
                            @Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin,
                            @Param("id") Long id);
}
