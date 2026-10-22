package escuela.personal;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaConceptosEmpleadoConfirmadaTest {
 private String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V124__guia_conceptos_por_empleado_confirmada.sql"));}
 @Test void diezEtapasConCuatroAccionesYCierreConfirmado()throws Exception{
  var sql=sql();var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/conceptos-empleado").acciones()).hasSize(4);
  }
assertThat(n).isEqualTo(10);assertThat(sql).contains("BON-PUNT-TEST","Bruce Wayne","DOC-PRUEBA-02","500 MXN desde 01/01/2027","600 desde 01/02/2027","Cancelada","31/01/2027","Inicio del nuevo importe","Referencia de la asignación anterior","Exportar Excel","Cancelar dentro del modal","Sin fecha de fin");
 }
 @Test void soloContenidoSinModificarPersonalSaldosOPermisos()throws Exception{
  var sql=sql();assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-22'","EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR","No repetir");
  assertThat(sql).doesNotContain("INSERT INTO concepto_empleado(","UPDATE concepto_empleado ","UPDATE condicion_salarial ","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM","INSERT INTO movimiento_financiero");
  assertThat(Pattern.compile("(?m)^UPDATE (\\w+)").matcher(sql).results().map(m->m.group(1)).toList()).containsExactly("guia_proceso_paso","guia_proceso");
 }
}
