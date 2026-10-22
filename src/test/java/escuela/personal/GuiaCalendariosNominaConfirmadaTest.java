package escuela.personal;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaCalendariosNominaConfirmadaTest {
 private String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V127__guia_calendarios_periodos_nomina_confirmada.sql"));}
 @Test void doceEtapasConCuatroAccionesYCierreExacto()throws Exception{
  var sql=sql();var pasos=Pattern.compile("(?m)^\\((\\d+),'[^']*','[^']*',\\R'([^']*)'").matcher(sql);int n=0;
  while(pasos.find()){assertThat(Integer.parseInt(pasos.group(1))).isEqualTo(++n);assertThat(new escuela.admin.service.GuiaProcesosService.Paso(n,"t","Administración","m",pasos.group(2),"e","r","p","/admin/periodos-nomina").acciones()).hasSize(4);}
  assertThat(n).isEqualTo(12);assertThat(sql).contains("NOM-MENSUAL-TEST","ENE-2027-TEST","FEB-2027-TEST","Enero de 2027 — revisión A","28/01/2027","Sin definir","Programado/Próximo","Cancelado/No aplica","Configuración avanzada","Fecha límite para recibir incidencias de nómina","TRASLAPE-2027-TEST","Exportar Excel","Volver al listado","Confirmar cancelación","La revisión B no se guardó");
 }
 @Test void soloDocumentacionPrivadaSinRepetirOperaciones()throws Exception{
  var sql=sql();assertThat(sql).contains("'CONFIRMADA'","DATE '2026-10-22'","EMPLEADO_LEER","EMPLEADO_LABORAL_LEER","CONDICION_SALARIAL_ADMINISTRAR","No repetir","no bloquea capturas","prorrateo automático");
  assertThat(sql).doesNotContain("INSERT INTO periodo_nomina(","UPDATE periodo_nomina ","INSERT INTO calendario_nomina(","UPDATE calendario_nomina ","UPDATE condicion_salarial ","INSERT INTO rol_permiso","CREATE TABLE","ALTER TABLE","DELETE FROM","INSERT INTO movimiento_financiero");
  assertThat(Pattern.compile("(?m)^UPDATE (\\w+)").matcher(sql).results().map(m->m.group(1)).toList()).containsExactly("guia_proceso_paso","guia_proceso");
 }
}
