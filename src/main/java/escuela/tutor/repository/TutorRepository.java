package escuela.tutor.repository;

import escuela.tutor.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface TutorRepository extends JpaRepository<Tutor, Long>, JpaSpecificationExecutor<Tutor> {
    boolean existsByUsuarioIdAndIdNot(Long usuarioId, Long id);

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
