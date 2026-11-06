package escuela.personal;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class NominaFiltrosCoberturaTest {
 @Test void consultasDeNominaTienenPosicionCompartidaSinMarcarOperacionesPost()throws Exception{
  var root=Path.of("src/main/resources/templates/admin");
  for(var file:List.of("empleados.html","puestos.html","conceptos-nomina.html","conceptos-empleado.html","laboral-catalogo.html","laboral-asignaciones.html","programacion-nomina-listado.html","configuracion-nomina.html","conceptos-periodo-nomina.html","empleado-puesto.html","nomina-listado.html","nomina-nuevo.html","nomina-pago-multiple.html","prestamos-empleado.html","nomina-pagos.html","nomina-pendientes.html","nomina-periodo-previa.html","fragments/empleado-nomina.html")){
   var html=Files.readString(root.resolve(file));var forms=Pattern.compile("<form\\b[^>]*>").matcher(html);int get=0;
   while(forms.find()){var form=forms.group();if(form.contains("method=\"get\"")){get++;assertThat(form).as(file).contains("data-nomina-keep-filter");}else assertThat(form).as(file).doesNotContain("data-nomina-keep-filter");}
   assertThat(get).as(file).isPositive();
   if(!file.startsWith("fragments/"))assertThat(html).as(file).satisfiesAnyOf(s->assertThat(s).contains("/js/nomina-filtros.js?v=20261106-scroll"),s->assertThat(s).contains("admin/fragments/nomina-comun :: head("));
  }
  assertThat(Files.readString(root.resolve("fragments/nomina-comun.html"))).contains("/js/nomina-filtros.js?v=20261106-scroll");
  assertThat(Files.readString(Path.of("src/main/resources/static/js/preparacion-nomina.js"))).doesNotContain("nomina-filtros-scroll:");
 }
}
