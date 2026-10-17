package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaReportesTraspasoContenidoTest {
 private String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V107__guia_reportes_traspaso_confirmados.sql"));}
 @Test void documentaSoloReportesConfirmadosYSuperaPendienteAnteriorSinMoverDinero() throws Exception {
  var texto=sql();
  assertThat(texto).contains("'CONFIRMADA'","TRASPASO-TEST-001","BANCO-COMPRAS-TEST","BANCO-TRASPASO-TEST",
   "$4,650.00","$906.00","$406.00","$5,150.00","$706.00","$1,700.00","$300.00",
   "este formulario no usa Desde/Hasta","Resumen","Estado de cuenta",
   "Reversa pendiente","REPORTE_FINANCIERO_CONSULTAR","WHERE slug='transferencia-entre-cuentas-confirmada'","AND numero=6");
  var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
  assertThat(tablas).hasSize(5).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
  assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","INSERT INTO rol_permiso","UPDATE cuenta_financiera","INSERT INTO movimiento_financiero");
 }
 @Test void seisEtapasConCuatroAccionesYReferenciasValidas() throws Exception {
  var texto=sql();
  var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(texto);int etapa=0;
  while(filas.find()) {
   assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
   assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p",null).acciones()).hasSize(4);
   var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(filas.group(4));int accion=0;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
   assertThat(accion).isEqualTo(4);
  }
  assertThat(etapa).isEqualTo(6);
  var controlador=Files.readString(Path.of("src/main/java/escuela/admin/controller/ReporteFinancieroAdminController.java"));
  assertThat(controlador).contains("@GetMapping(\"/tesoreria\")","@GetMapping(\"/estado-cuenta-cuenta\")");
 }
}
