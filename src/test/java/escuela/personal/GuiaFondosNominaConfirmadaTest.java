package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class GuiaFondosNominaConfirmadaTest {
 @Test void nuevePasosRealesDistinguenCancelarDeConfirmarSinOperaciones()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V144__guia_nomina_fondos_insuficientes_confirmada.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);}
  assertThat(n).isEqualTo(9);assertThat(sql).contains("'CONFIRMADA'","BOR-000006","NPI-7","$725.71","$5,806.45","$10,956.40","$274.29","Cancelar dentro del modal","Incluir","Preparar pago colectivo","La prueba de dos pestañas sigue pendiente");
  assertThat(sql).doesNotContain("INSERT INTO nomina_","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
}
