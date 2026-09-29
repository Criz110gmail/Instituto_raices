package escuela.academico.repository;

import escuela.academico.entity.MateriaGrado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface MateriaGradoRepository extends JpaRepository<MateriaGrado, Long> {
    List<MateriaGrado> findAllByMateriaIdOrderByActivoDescOrdenAsc(Long materiaId);
    Optional<MateriaGrado> findByIdAndMateriaId(Long id, Long materiaId);
    boolean existsByMateriaIdAndGradoId(Long materiaId, Long gradoId);
    boolean existsByMateriaIdAndGradoIdAndActivoTrue(Long materiaId, Long gradoId);
    boolean existsByGradoIdAndOrdenAndActivoTrueAndIdNot(Long gradoId, Integer orden, Long id);
    long countByMateriaIdAndActivoTrue(Long materiaId);

    @Query("""
            select mg from MateriaGrado mg
            join mg.materia m
            join Grupo g on g.grado.id = mg.grado.id
            where g.id = :grupoId and mg.activo = true and m.activo = true
              and (lower(m.codigo) like concat('%', lower(:texto), '%')
                or lower(m.nombre) like concat('%', lower(:texto), '%'))
            order by mg.orden, m.nombre, mg.id
            """)
    Slice<MateriaGrado> buscarParaCalificaciones(@Param("grupoId") Long grupoId,
                                                  @Param("texto") String texto,
                                                  Pageable limite);
}
