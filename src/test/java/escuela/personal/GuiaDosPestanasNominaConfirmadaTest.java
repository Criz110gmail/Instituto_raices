package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class GuiaDosPestanasNominaConfirmadaTest {
 @Test void nuevePasosRealesDocumentanSoloUnaSalidaSinOperarDinero()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V145__guia_nomina_dos_pestanas_confirmada.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);}
  assertThat(n).isEqualTo(9);assertThat(sql).contains("'CONFIRMADA'","BOR-000007","NPI-8","$6,193.55","$4,762.85","DIC-BRUCE-DOBLE-A","La nómina cambió","antes de la primera confirmación","sin recargar","Historial de intentos","transacciones simultáneas");
  assertThat(sql).doesNotContain("INSERT INTO nomina_","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
}
