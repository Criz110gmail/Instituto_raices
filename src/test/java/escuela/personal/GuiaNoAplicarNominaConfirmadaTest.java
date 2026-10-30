package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class GuiaNoAplicarNominaConfirmadaTest {
 @Test void explicaSinTecnicismosLaExclusionSinPagoNiPendiente()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V147__guia_nomina_no_aplicar_empleado_confirmada.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);}
  assertThat(n).isEqualTo(6);assertThat(sql).contains("'CONFIRMADA'","BOR-000010","$557.42","$516.13","$41.29","Confirmada sin pagos","no sale dinero","no queda pendiente","se lo pagaré después","Historial de decisiones","Confirmar decisiones sin mover dinero");
  assertThat(sql).doesNotContain("INSERT INTO nomina_","UPDATE cuenta_financiera","UPDATE concepto_empleado","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
}
