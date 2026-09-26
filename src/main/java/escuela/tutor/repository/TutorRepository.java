package escuela.tutor.repository;

import escuela.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long>, JpaSpecificationExecutor<Tutor> {
    Optional<Tutor> findByUsuarioIdAndInstitucionIdAndActivoTrue(Long usuarioId, Long institucionId);
    boolean existsByUsuarioIdAndIdNot(Long usuarioId, Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Tutor t where t.id = :id")
    Optional<Tutor> findByIdForUpdate(@Param("id") Long id);

    @Query(value = """
            SELECT t.* FROM tutor t
            WHERE t.institucion_id = :institucionId
              AND t.activo = true
              AND t.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%')
            ORDER BY t.primer_apellido, t.segundo_apellido NULLS LAST, t.nombres, t.id
            """, nativeQuery = true)
    Slice<Tutor> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                          @Param("texto") String texto,
                                          Pageable limite);
}
