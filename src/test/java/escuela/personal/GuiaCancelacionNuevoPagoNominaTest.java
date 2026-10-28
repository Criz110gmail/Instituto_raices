package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaCancelacionNuevoPagoNominaTest {
 @Test void documentaOchoPasosConCuatroAccionesYNoOperaDinero()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V141__guia_cancelacion_y_nuevo_pago_nomina_confirmada.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(8);assertThat(sql).contains("'CONFIRMADA'","NPI-1","NPI-2","15:32","16:41","$14,187.15","$23,330.00","$12,762.85","$1,150.00","REPAGO-NOM-RED-JUL-01","Editar distribución","Cancelar dentro del modal","Pago cancelado","REVERTIDO","EMPLEADO_NOMINA_LEER","No volver a confirmar","no confirma pagos individuales");
  assertThat(sql).doesNotContain("INSERT INTO nomina_pago","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
  assertThat(Pattern.compile("(?m)^UPDATE (\\w+)").matcher(sql).results().map(m->m.group(1)).toList()).containsExactly("guia_proceso_paso","guia_proceso");
 }
}
