package escuela.docente.support;

import escuela.docente.repository.AsignacionMaestroRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

class AsignacionFechaAbiertaTest {
 @Test void rangoAbiertoNoSeConvierteEnUnDiaYFechasNulasTienenTipoExplicito()throws Exception{
  var query=AsignacionMaestroRepository.class.getMethod("existeTraslape",Long.class,Long.class,Long.class,LocalDate.class,LocalDate.class,Long.class).getAnnotation(Query.class);
  assertThat(query.nativeQuery()).isTrue();
  assertThat(query.value()).contains("cast(:fechaFin as date) is null","a.fecha_inicio<=cast(:fechaFin as date)","a.fecha_fin>=cast(:fechaInicio as date)","a.activo=true","a.id<>:id").doesNotContain("coalesce");
 }
}
