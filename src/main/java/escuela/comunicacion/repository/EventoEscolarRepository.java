package escuela.comunicacion.repository;

import escuela.comunicacion.entity.EventoEscolar;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EventoEscolarRepository extends JpaRepository<EventoEscolar, Long>,
        JpaSpecificationExecutor<EventoEscolar> {
    @Override
    @EntityGraph(attributePaths = {"institucion", "cicloEscolar", "plantel"})
    Page<EventoEscolar> findAll(Specification<EventoEscolar> spec, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from EventoEscolar e where e.id=:id")
    Optional<EventoEscolar> findByIdForUpdate(@Param("id") Long id);

    @EntityGraph(attributePaths = {"institucion", "cicloEscolar", "plantel", "destinatarios",
            "destinatarios.nivelEducativo", "destinatarios.grado", "destinatarios.grupo",
            "destinatarios.alumno"})
    @Query("select e from EventoEscolar e where e.id=:id")
    Optional<EventoEscolar> findDetalleById(@Param("id") Long id);
}
