package escuela.personal;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaPagoIndividualNominaConfirmadoTest {
 @Test void nuevePasosDetalladosConfirmanPagoYBloqueoSinOperarDinero()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V142__guia_pago_individual_nomina_confirmado.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/preparacion-nomina").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(9);assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-29'","BOR-000003","NPI-3","08:44","$6,975.71","$7,190.00","$214.29","$16,354.29","$12,762.85","$1,150.00","IND-DIANA-SEP2027-Q1","Editar distribución","Cancelar dentro del modal","EMPLEADO_NOMINA_LEER","No aplicar/Posponer","sin exigir borrador previo");
  assertThat(sql).doesNotContain("INSERT INTO nomina_pago","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM");
 }
 @Test void avisoDistinguePagoDeReservaSinEliminarElBloqueo()throws Exception{
  var java=Files.readString(Path.of("src/main/java/escuela/personal/service/PreparacionNominaService.java"));
  assertThat(java).contains("ni.estado='PAGADO'","WHEN ya_pagado AND NOT :simulacion","Ya tiene un pago registrado para estas fechas","WHEN ocupado AND NOT :simulacion","Ya existe un borrador o una nómina para este empleado","ne.borrador_id<>:ignorar","ne.activo");
 }
}
