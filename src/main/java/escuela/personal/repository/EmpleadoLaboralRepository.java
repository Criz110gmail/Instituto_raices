package escuela.personal.repository;
import escuela.personal.entity.EmpleadoLaboral;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface EmpleadoLaboralRepository extends JpaRepository<EmpleadoLaboral,Long>{
 Optional<EmpleadoLaboral> findByEmpleadoId(Long empleadoId);
 interface ResumenFila {
  Long getEmpleadoId(); String getSituacion(); java.time.LocalDate getIngreso(); java.time.LocalDate getBaja();
 }
 @org.springframework.data.jpa.repository.Query("select l.empleado.id as empleadoId, l.situacion as situacion, l.fechaIngreso as ingreso, l.fechaBaja as baja from EmpleadoLaboral l where l.empleado.institucion.id = :institucion and l.empleado.id in :ids")
 java.util.List<ResumenFila> resumirPagina(@org.springframework.data.repository.query.Param("institucion")Long institucion,@org.springframework.data.repository.query.Param("ids")java.util.Collection<Long> ids);
}
