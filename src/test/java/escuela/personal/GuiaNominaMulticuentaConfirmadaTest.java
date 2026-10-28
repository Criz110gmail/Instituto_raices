package escuela.personal;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaNominaMulticuentaConfirmadaTest {
 @Test void guiaConfirmaSoloPagoYSimuladorConOchoPasosDetallados()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V140__guia_nomina_colectiva_dos_cuentas_confirmada.sql"));
  var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/empleados").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(8);
  assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-28'","BOR-000002","JUL-2027-RED-TEST","Diana Prueba Puestos","$14,187.15","$23,330.00","$12,762.85","NPI-1","Simular nómina","Cancelar dentro del modal","EMPLEADO_NOMINA_LEER","CONDICION_SALARIAL_LEER","no crees otro borrador","Deja pendiente la prueba independiente");
  assertThat(sql).doesNotContain("INSERT INTO nomina_pago","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero","CREATE TABLE","ALTER TABLE","INSERT INTO rol_permiso","DELETE FROM");
 }
 @Test void accesoDelExpedienteConservaPermisosYRutaYEstiloDeReferencia()throws Exception{
  var html=Files.readString(Path.of("src/main/resources/templates/admin/empleado-form.html"));
  assertThat(html).contains("edicion and puedeLaboral and puedeNomina == true","employee-nomina-access","/admin/empleados/{id}/nomina");
  assertThat(Files.readString(Path.of("src/main/resources/static/css/forms.css"))).contains(".student-tablist .employee-nomina-access { display:flex;flex-direction:column","background:var(--aqua);color:#062b31");
 }
}
