package escuela.institucion.repository;

import escuela.institucion.entity.Institucion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface InstitucionRepository extends JpaRepository<Institucion, Long>, JpaSpecificationExecutor<Institucion> {
    Optional<Institucion> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Institucion i where i.id = :id")
    Optional<Institucion> buscarPorIdConBloqueo(@Param("id") Long id);
}
