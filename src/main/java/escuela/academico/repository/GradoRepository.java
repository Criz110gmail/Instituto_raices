package escuela.academico.repository;

import escuela.academico.entity.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface GradoRepository extends JpaRepository<Grado, Long>, JpaSpecificationExecutor<Grado> {
    List<Grado> findAllByNivelEducativoIdOrderByOrdenAsc(Long nivelEducativoId);
    Optional<Grado> findByNivelEducativoIdAndCodigoIgnoreCase(Long nivelEducativoId, String codigo);
    boolean existsByNivelEducativoIdAndCodigoIgnoreCaseAndIdNot(Long nivelEducativoId, String codigo, Long id);
    boolean existsByNivelEducativoIdAndOrdenAndIdNot(Long nivelEducativoId, int orden, Long id);
    @Query(value = """
            SELECT g.* FROM grado g
            JOIN nivel_educativo n ON n.id = g.nivel_educativo_id
            WHERE n.institucion_id = :institucionId
              AND g.activo = true AND n.activo = true
              AND lower(g.codigo || ' ' || g.nombre || ' ' || n.nombre) LIKE ('%' || lower(:texto) || '%')
            ORDER BY n.orden, g.orden, g.id
            """, nativeQuery = true)
    Slice<Grado> buscarParaMateria(@Param("institucionId") Long institucionId,
                                   @Param("texto") String texto, Pageable limite);
}
