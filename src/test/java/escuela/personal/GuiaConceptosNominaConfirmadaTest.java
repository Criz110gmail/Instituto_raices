package escuela.personal;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaConceptosNominaConfirmadaTest {
 private String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V122__guia_conceptos_nomina_confirmada.sql"));}
 @Test void diezEtapasConAccionesModulosYCasoFinalActivo()throws Exception{
  var sql=sql();var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){
   assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);
   assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/conceptos-nomina").acciones()).hasSize(4);
  }
  assertThat(n).isEqualTo(10);assertThat(sql).contains("BON-PUNT-TEST","DESC-TEST","APORT-TEST","los tres conceptos Activos","dos pestañas","Cancelar dentro del modal","Exportar Excel","Confirmar y guardar");
 }
 @Test void soloEditorialSinOperarCatalogoNiOtorgarPermisos()throws Exception{
  var sql=sql();assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-21'","EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR","No repetir");
  assertThat(sql).doesNotContain("INSERT INTO concepto_nomina(","UPDATE concepto_nomina ","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM","INSERT INTO movimiento_financiero");
  assertThat(Pattern.compile("(?m)^UPDATE (\\w+)").matcher(sql).results().map(m->m.group(1)).toList()).containsExactly("guia_proceso_paso","guia_proceso");
 }
}
