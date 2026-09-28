package escuela.asistencia.repository;

import escuela.asistencia.entity.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long>, JpaSpecificationExecutor<Asistencia> {
    List<Asistencia> findAllByInscripcionIdInAndGrupoIdAndFecha(Collection<Long> inscripcionIds,
                                                                 Long grupoId, LocalDate fecha);
}
