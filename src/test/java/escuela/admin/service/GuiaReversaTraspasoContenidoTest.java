package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaReversaTraspasoContenidoTest {
 private String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V108__guia_reversa_traspaso_reportes_confirmados.sql"));}
 @Test void registraEvidenciaSinOperacionesYDistingueDosPendientesHumanos() throws Exception {
  var texto=sql();
  assertThat(texto).contains("'CONFIRMADA'","TRASPASO-TEST-001","BANCO-COMPRAS-TEST","BANCO-TRASPASO-TEST",
   "$2,000.00","$0.00","$300.00","$706.00","$5,150.00","16:10","Compensa #6","Compensa #7",
   "No se proporcionó el ID","Confirmar y registrar reversa","fecha y hora real","texto exacto guardado no fue mostrado",
   "cuatro movimientos","dos movimientos","NO resultados humanos confirmados","bloqueo de segunda reversión",
   "Mensual","no hay campos Desde/Hasta","REPORTE_FINANCIERO_CONSULTAR","ANTERIOR a la reversión",
   "'transferencia-entre-cuentas-confirmada','reportes-traspaso-confirmados'");
  var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
  assertThat(tablas).hasSize(6).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
  assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","INSERT INTO rol_permiso","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero");
 }
 @Test void sieteEtapasConCuatroAccionesCadaUna() throws Exception {
  var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());int etapa=0;
  while(filas.find()) {
   assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
   assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p",null).acciones()).hasSize(4);
   var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(filas.group(4));int accion=0;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
   assertThat(accion).isEqualTo(4);
  }
  assertThat(etapa).isEqualTo(7);
 }
}
