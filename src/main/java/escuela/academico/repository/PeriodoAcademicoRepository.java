package escuela.academico.repository;

import escuela.academico.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDate;
import java.util.List;

public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Long>, JpaSpecificationExecutor<PeriodoAcademico> {
    List<PeriodoAcademico> findAllByCicloEscolarIdAndNivelEducativoIdOrderByOrdenAsc(
            Long cicloEscolarId, Long nivelEducativoId);

    boolean existsByCicloEscolarIdAndNivelEducativoIdAndCodigoIgnoreCaseAndIdNot(
            Long cicloEscolarId, Long nivelEducativoId, String codigo, Long id);

    boolean existsByCicloEscolarIdAndNivelEducativoIdAndOrdenAndIdNot(
            Long cicloEscolarId, Long nivelEducativoId, int orden, Long id);

    @Query("""
            select (count(p) > 0) from PeriodoAcademico p
             where p.cicloEscolar.id = :cicloId
               and p.nivelEducativo.id = :nivelId
               and p.id <> :idExcluido
               and p.fechaInicio <= :fechaFin
               and p.fechaFin >= :fechaInicio
            """)
    boolean existeSolapamiento(@Param("cicloId") Long cicloId,
                               @Param("nivelId") Long nivelId,
                               @Param("fechaInicio") LocalDate fechaInicio,
                               @Param("fechaFin") LocalDate fechaFin,
                               @Param("idExcluido") Long idExcluido);

    @Query("""
            select (count(p) > 0) from PeriodoAcademico p
             where p.cicloEscolar.id = :cicloId
               and (p.fechaInicio < :fechaInicio or p.fechaFin > :fechaFin)
            """)
    boolean existenFueraDeRango(@Param("cicloId") Long cicloId,
                                @Param("fechaInicio") LocalDate fechaInicio,
                                @Param("fechaFin") LocalDate fechaFin);

    @Query(value = """
            SELECT p.* FROM periodo_academico p
            JOIN inscripcion i ON i.ciclo_escolar_id = p.ciclo_escolar_id
            JOIN grado g ON g.id = i.grado_id AND g.nivel_educativo_id = p.nivel_educativo_id
            WHERE i.id = :inscripcionId
              AND lower(p.codigo || ' ' || p.nombre) LIKE ('%' || lower(:texto) || '%')
            ORDER BY p.orden, p.id
            """, nativeQuery = true)
    Slice<PeriodoAcademico> buscarParaCargo(@Param("inscripcionId") Long inscripcionId,
                                             @Param("texto") String texto,
                                             Pageable limite);
}
