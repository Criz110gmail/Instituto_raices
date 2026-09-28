package escuela.calificacion.repository;

import escuela.calificacion.entity.Calificacion;
import escuela.calificacion.entity.EstadoCalificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long>,
        JpaSpecificationExecutor<Calificacion> {
    List<Calificacion> findAllByInscripcionIdInAndMateriaGradoIdAndPeriodoAcademicoId(
            Collection<Long> inscripcionIds, Long materiaGradoId, Long periodoId);

    Optional<Calificacion> findByInscripcionIdAndMateriaGradoIdAndPeriodoAcademicoId(
            Long inscripcionId, Long materiaGradoId, Long periodoId);

    @Query("""
            select c from Calificacion c
            where c.inscripcion.alumno.id = :alumnoId
              and c.estado = :estado
            order by c.periodoAcademico.cicloEscolar.fechaInicio desc,
                     c.periodoAcademico.orden desc, c.materiaGrado.orden asc, c.id
            """)
    Page<Calificacion> buscarPortal(@Param("alumnoId") Long alumnoId,
                                    @Param("estado") EstadoCalificacion estado,
                                    Pageable pageable);

    @EntityGraph(attributePaths = {"materiaGrado", "materiaGrado.materia", "periodoAcademico"})
    List<Calificacion> findAllByInscripcionIdInAndEstadoAndMateriaGradoIncluirBoletaTrueOrderByInscripcionIdAscPeriodoAcademicoOrdenAscMateriaGradoOrdenAscIdAsc(
            Collection<Long> inscripcionIds, EstadoCalificacion estado);
}
