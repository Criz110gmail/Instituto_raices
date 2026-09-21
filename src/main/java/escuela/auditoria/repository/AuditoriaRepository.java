package escuela.auditoria.repository;

import escuela.auditoria.entity.Auditoria;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long>, JpaSpecificationExecutor<Auditoria> {
    @Override
    @EntityGraph(attributePaths = {"institucion", "usuarioActor"})
    Page<Auditoria> findAll(@Nullable Specification<Auditoria> spec, Pageable pageable);
}
