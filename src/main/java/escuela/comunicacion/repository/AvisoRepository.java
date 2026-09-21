package escuela.comunicacion.repository;

import escuela.comunicacion.entity.Aviso;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AvisoRepository extends JpaRepository<Aviso, Long>, JpaSpecificationExecutor<Aviso> {
    @Override @EntityGraph(attributePaths = {"institucion", "plantel"})
    Page<Aviso> findAll(Specification<Aviso> spec, Pageable pageable);
    @EntityGraph(attributePaths = {"institucion", "plantel"})
    @Query("select a from Aviso a where a.id=:id") Optional<Aviso> findDetalleById(@Param("id") Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Aviso a where a.id=:id") Optional<Aviso> findByIdForUpdate(@Param("id") Long id);
}
