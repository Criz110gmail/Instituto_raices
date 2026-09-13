package escuela.alumno.repository;

import escuela.alumno.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long>, JpaSpecificationExecutor<Alumno> {
    boolean existsByInstitucionIdAndMatriculaIgnoreCaseAndIdNot(Long institucionId, String matricula, Long id);
    boolean existsByInstitucionIdAndCurpIgnoreCaseAndIdNot(Long institucionId, String curp, Long id);
    List<Alumno> findAllByInstitucionIdAndActivoTrueOrderByPrimerApellidoAscSegundoApellidoAscNombresAsc(
            Long institucionId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Alumno a where a.id = :id")
    Optional<Alumno> findByIdForUpdate(@Param("id") Long id);
}
