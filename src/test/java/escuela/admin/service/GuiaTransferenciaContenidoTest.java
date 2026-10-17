package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaTransferenciaContenidoTest {
 private String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V106__guia_transferencia_entre_cuentas_confirmada.sql"));}
 @Test void aceptaSoloTransferenciaEvidenciadaSinModificarOperaciones() throws Exception {
  var texto=sql();
  assertThat(texto).contains("'CONFIRMADA'","BANCO-COMPRAS-TEST","BANCO-TRASPASO-TEST","TRASPASO-TEST-001",
   "$2,000.00","$1,700.00","$300.00","20:01","transferencias/1/nueva","no se proporcionaron IDs",
   "No incluye aceptación de reversa ni reportes","TRANSFERENCIA_CUENTA_REGISTRAR");
  var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
  assertThat(tablas).hasSize(3).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
  assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","INSERT INTO rol_permiso","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero");
 }
 @Test void seisEtapasConCuatroAccionesContiguas() throws Exception {
  var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());int etapa=0;
  while(filas.find()){
   assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
   assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p",null).acciones()).hasSize(4);
   var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(filas.group(4));int accion=0;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
   assertThat(accion).isEqualTo(4);
  }
  assertThat(etapa).isEqualTo(6);
 }
}
