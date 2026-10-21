package escuela.personal;

import escuela.admin.dto.ModuloCatalogo;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class NombresConfiguracionLaboralTest {
 @Test void nombreVisibleNoCambiaRutaNiIdentificador(){
  assertThat(ModuloCatalogo.ESQUEMAS_REMUNERACION.titulo()).isEqualTo("Puestos y sueldos configurados");
  assertThat(ModuloCatalogo.ESQUEMAS_REMUNERACION.rutaListado()).isEqualTo("/admin/esquemas");
 }
 @Test void capturasFiltrosYDetalleUsanMismoVocabulario()throws Exception{
  for(String archivo:new String[]{"laboral-asignacion-form.html","laboral-asignaciones.html","laboral-catalogo.html"}){
   var html=Files.readString(Path.of("src/main/resources/templates/admin",archivo));
   assertThat(html).contains("Configuración de puesto y sueldo").doesNotContain("Esquema de remuneración","Nuevo esquema");
  }
  var catalogo=Files.readString(Path.of("src/main/resources/templates/admin/laboral-catalogo.html"));
  assertThat(catalogo).contains("Nueva configuración","Define un puesto, su jornada y su sueldo");
  assertThat(Files.readString(Path.of("src/main/resources/templates/admin/laboral-asignacion-detalle.html"))).contains("Configuración histórica");
 }
}
