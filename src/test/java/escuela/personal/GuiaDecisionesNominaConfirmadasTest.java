package escuela.personal;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaDecisionesNominaConfirmadasTest {
 @Test void quincePasosConRutasRealesYCasosAceptadosSinOperacionesFinancieras()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V143__guia_decisiones_y_pospuestos_nomina_confirmados.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(15);
  assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-29'","BOR-000004","BOR-000005","NPI-5","NPI-6","Detalle por empleado","Decisiones y pendientes de este periodo","Preparar pago individual","Editar distribución","No aplicar","Posponer","$4,274.29","$4,354.29","$12,762.85","$1,150.00","EMPLEADO_NOMINA_LEER","Fondos insuficientes y concurrencia requieren ensayos independientes");
  assertThat(sql).doesNotContain("INSERT INTO nomina_","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
}
