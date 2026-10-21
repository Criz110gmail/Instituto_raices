package escuela.personal;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaAsignacionesConfirmadaTest {
 private String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V120__guia_asignaciones_laborales_confirmada.sql"));}
 @Test void doceEtapasConModulosEjemplosYAccionesSeparadas()throws Exception{
  var sql=sql();var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/asignaciones").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(12);assertThat(sql).contains("Puestos y sueldos configurados","Nueva configuración","Configuración de puesto y sueldo","Cancelar dentro del modal","Excel","30 horas","25 horas","12000","13000","01/02/2027","Sin fecha de fin");
 }
 @Test void soloEditorialSinConcederPermisosNiModificarSueldos()throws Exception{
  var sql=sql();assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-21'","EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR","No repetir","todavía no es un paso operativo");
  assertThat(sql).doesNotContain("CREATE TABLE","ALTER TABLE","INSERT INTO condicion_salarial","UPDATE condicion_salarial","UPDATE empleado","INSERT INTO rol_permiso","DELETE FROM","INSERT INTO movimiento_financiero");
  assertThat(Pattern.compile("(?m)^UPDATE (\\w+)").matcher(sql).results().map(m->m.group(1)).toList()).containsExactly("guia_proceso_paso","guia_proceso");
 }
}
