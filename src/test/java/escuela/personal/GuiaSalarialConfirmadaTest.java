package escuela.personal;
import org.junit.jupiter.api.Test;import java.nio.file.*;import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;
class GuiaSalarialConfirmadaTest {
 @Test void ochoPasosDetalladosSoloEditorialesSinCambiarSueldos()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V118__guia_condiciones_salariales_confirmada.sql"));
  assertThat(sql).contains("'CONFIRMADA'","2026-10-21","12000","13000","Cancelar programación","No repetir operaciones","CONDICION_SALARIAL_ADMINISTRAR").doesNotContain("UPDATE empleado","INSERT INTO condicion_salarial","INSERT INTO rol_permiso","DELETE FROM");
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/empleados").acciones()).hasSize(4);}assertThat(n).isEqualTo(8);
 }
}
