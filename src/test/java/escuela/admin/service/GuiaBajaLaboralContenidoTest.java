package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaBajaLaboralContenidoTest {
 private String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V114__guia_baja_laboral.sql"));}
 @Test void documentaSinOperarPersonalNiOtorgarRoles()throws Exception{
  var t=sql();assertThat(Pattern.compile("(?im)^(?:INSERT INTO|UPDATE) (\\w+)").matcher(t).results().map(m->m.group(1)).toList()).containsExactlyInAnyOrder("guia_proceso","guia_proceso_permiso","guia_proceso_paso","guia_proceso_paso","guia_proceso_paso","guia_proceso");
  assertThat(t).contains("'CONFIRMADA'","DATE '2026-10-20'","'Personal'","'EMPLEADO_LEER'","'EMPLEADO_LABORAL_ADMINISTRAR'");
  assertThat(t).doesNotContain("UPDATE empleado","DELETE FROM","ALTER TABLE","INSERT INTO empleado ","INSERT INTO rol_permiso");
 }
 @Test void ochoEtapasDetalladasConLimitesDeAceptacion()throws Exception{
  var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());int etapa=0;
  while(filas.find()){assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p","/admin/empleados").acciones()).hasSize(4);}
  assertThat(etapa).isEqualTo(8);assertThat(sql()).contains("Cancelar dentro de esa ventana","Confirmar y guardar","ESTE TEXTO NO DEBE GUARDARSE","13:24","01/10/2026","20/10/2026","Expediente Activo, mostrado en verde","no cierra adscripciones","permisos de información laboral","no se considera validado aquí el caso de un maestro");
 }
}
