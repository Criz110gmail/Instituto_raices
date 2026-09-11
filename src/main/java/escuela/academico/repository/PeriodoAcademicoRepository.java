package escuela.academico.repository;

import escuela.academico.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
