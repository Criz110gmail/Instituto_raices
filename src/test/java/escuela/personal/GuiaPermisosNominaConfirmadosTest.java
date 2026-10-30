package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class GuiaPermisosNominaConfirmadosTest {
 @Test void seisPasosConservanSeparacionYRestauracionSinConcederPermisos()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V148__guia_permisos_nomina_confirmados.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/roles").acciones()).hasSize(4);}
  assertThat(n).isEqualTo(6);assertThat(sql).contains("'CONFIRMADA'","PRUEBA_PERSONAL_BASE","QA_SALARIOS_CONSULTA","incógnito","Empleados — Configuración de nómina","Excel","sin letra s final","vuelve a marcar","también permiten administrar","BOR-000010");
  assertThat(sql).doesNotContain("INSERT INTO usuario","UPDATE usuario","INSERT INTO rol_permiso","UPDATE rol_permiso","INSERT INTO movimiento_financiero","INSERT INTO nomina_","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
}
