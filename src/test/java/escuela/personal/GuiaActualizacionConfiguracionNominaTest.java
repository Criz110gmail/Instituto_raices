package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class GuiaActualizacionConfiguracionNominaTest {
 @Test void ochoPasosDocumentanRevisionesYRechazoSinOperacionesFinancieras()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V146__guia_actualizacion_configuracion_nomina_confirmada.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);}
  assertThat(n).isEqualTo(8);assertThat(sql).contains("'CONFIRMADA'","BOR-000009","$483.87","$522.58","pestaña A","Pestaña B","sin recargar","Cancelar programación futura","No se guardó nada","no identifica por nombre","registros 10 y 11","sin revisión 3");
  assertThat(sql).doesNotContain("INSERT INTO nomina_","UPDATE cuenta_financiera","UPDATE concepto_empleado","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
}
