package escuela.alumno.repository;

import escuela.alumno.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {
    boolean existsByInstitucionIdAndMatriculaIgnoreCaseAndIdNot(Long institucionId, String matricula, Long id);
    boolean existsByInstitucionIdAndCurpIgnoreCaseAndIdNot(Long institucionId, String curp, Long id);
    @Query(value = """
            SELECT a.* FROM alumno a
            WHERE a.institucion_id = :institucionId
              AND a.activo = true
              AND a.busqueda_autocomplete LIKE ('%' || lower(:texto) || '%')
            ORDER BY a.primer_apellido, a.segundo_apellido NULLS LAST, a.nombres, a.id
            """, nativeQuery = true)
    Slice<Alumno> buscarParaAutocompletado(@Param("institucionId") Long institucionId,
                                           @Param("texto") String texto,
                                           Pageable limite);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Alumno a where a.id = :id")
    Optional<Alumno> findByIdForUpdate(@Param("id") Long id);
}
