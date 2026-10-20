package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaPermisosLaboralesContenidoTest {
 private String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V115__guia_permisos_laborales.sql"));}
 @Test void soloEscribeContenidoSinConcederRolesNiModificarPersonal()throws Exception{
  var t=sql();
  assertThat(Pattern.compile("(?im)^(?:INSERT INTO|UPDATE) (\\w+)").matcher(t).results().map(m->m.group(1)).toList())
   .containsExactly("guia_proceso","guia_proceso_permiso","guia_proceso_paso","guia_proceso_paso","guia_proceso");
  assertThat(t).contains("'CONFIRMADA'","'EMPLEADO_LEER'","'ROL_ADMINISTRAR'","'USUARIO_ADMINISTRAR'");
  assertThat(t).doesNotContain("INSERT INTO rol_permiso","UPDATE empleado","DELETE FROM","ALTER TABLE");
 }
 @Test void ochoEtapasDeCuatroAccionesConEvidenciaYLimites()throws Exception{
  var t=sql();var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(t);int n=0;
  while(filas.find()){
   assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++n);
   assertThat(new GuiaProcesosService.Paso(n,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p","/admin/empleados").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(8);
  assertThat(t).contains("Cancelar el modal conserva la captura","Los permisos de otros roles se suman",
   "Toda la institución","cierra sesión, inicia nuevamente","Excel respeta el filtro",
   "Código laboral, Nombre, Correo de contacto, Teléfono, Expediente, Código docente y Perfil docente",
   "#laboral-empleado","no acredita todos los endpoints privados","no hay selección visual independiente de sólo lectura",
   "todavía no existen formularios de sueldo","no repitas operaciones ya confirmadas");
 }
}
