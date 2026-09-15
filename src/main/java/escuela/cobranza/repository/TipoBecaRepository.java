package escuela.cobranza.repository;

import escuela.cobranza.entity.TipoBeca;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface TipoBecaRepository extends JpaRepository<TipoBeca, Long>, JpaSpecificationExecutor<TipoBeca> {
    boolean existsByInstitucionIdAndCodigoIgnoreCaseAndIdNot(Long institucionId, String codigo, Long id);

    @Query(value = """
            SELECT t.* FROM tipo_beca t
            WHERE t.institucion_id = :institucionId AND t.activo = true
              AND t.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%')
            ORDER BY t.nombre, t.id
            """, nativeQuery = true)
    Slice<TipoBeca> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                              @Param("texto") String texto, Pageable limite);
}
