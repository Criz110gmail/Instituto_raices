package escuela.alumno.repository;

import escuela.alumno.entity.AlumnoTutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlumnoTutorRepository extends JpaRepository<AlumnoTutor, Long>,
        JpaSpecificationExecutor<AlumnoTutor> {

    List<AlumnoTutor> findAllByAlumnoIdAndTutorIdAndActivoTrueAndIdNot(
            Long alumnoId, Long tutorId, Long id);

    List<AlumnoTutor> findAllByAlumnoIdAndContactoPrincipalTrueAndActivoTrueAndIdNot(
            Long alumnoId, Long id);

    @Query("""
            select (count(v) > 0) from AlumnoTutor v
            where v.alumno.id = :alumnoId and v.tutor.id = :tutorId
              and v.activo = true and v.responsableFinanciero = true
              and v.fechaInicio <= :fecha
              and (v.fechaFin is null or v.fechaFin >= :fecha)
            """)
    boolean tieneResponsabilidadFinancieraVigente(@Param("alumnoId") Long alumnoId,
                                                   @Param("tutorId") Long tutorId,
                                                   @Param("fecha") LocalDate fecha);
}
