package escuela.alumno.repository;

import escuela.alumno.entity.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ActualizacionExpedienteFamiliarRepository extends
        JpaRepository<ActualizacionExpedienteFamiliar, Long>,
        JpaSpecificationExecutor<ActualizacionExpedienteFamiliar> {
    Optional<ActualizacionExpedienteFamiliar> findByIdAndTutorId(Long id, Long tutorId);
    boolean existsByAlumnoIdAndTipoAndEstado(Long alumnoId, TipoActualizacionExpediente tipo,
                                             EstadoActualizacionExpediente estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from ActualizacionExpedienteFamiliar a where a.id=:id")
    Optional<ActualizacionExpedienteFamiliar> findByIdForUpdate(@Param("id") Long id);
}
