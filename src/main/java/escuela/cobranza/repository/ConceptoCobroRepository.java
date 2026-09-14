package escuela.cobranza.repository;

import escuela.cobranza.entity.ConceptoCobro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConceptoCobroRepository extends JpaRepository<ConceptoCobro, Long>,
        JpaSpecificationExecutor<ConceptoCobro> {

    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(
            Long institucionId, String codigo, Long id);

    @Query(value = """
            SELECT c.* FROM concepto_cobro c
            WHERE c.institucion_id = :institucionId AND c.activo = true
              AND c.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%')
            ORDER BY c.nombre, c.id
            """, nativeQuery = true)
    Slice<ConceptoCobro> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                                   @Param("texto") String texto,
                                                   Pageable limite);
}
