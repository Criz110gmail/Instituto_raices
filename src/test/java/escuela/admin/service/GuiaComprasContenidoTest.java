package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaComprasContenidoTest {
 private String sql() throws Exception {
  return Files.readString(Path.of("src/main/resources/db/migration/V105__guia_compras_confirmacion_reversa_filtros.sql"));
 }
 @Test void evidenciaAceptadaSinOperacionesNiAceptacionesInventadas() throws Exception {
  var texto=sql();
  assertThat(texto).contains("'CONFIRMADA'","COM-00000001","Plantel prueba de permisos","BANCO-COMPRAS-TEST",
   "Papelería Prueba Raíces","$406.00","$1,594.00","$2,000.00","Compensa #4","Movimiento debe indicar 4",
   "no está vacío","No se conoce el ID de la reversa",
   "aceptación visual del nuevo archivo todavía pendiente","PROVEEDOR_LEER","COMPRA_CANCELAR");
  var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
  assertThat(tablas).hasSize(3).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
  assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","INSERT INTO rol_permiso","INSERT INTO compra ","UPDATE cuenta_financiera");
 }
 @Test void ochoEtapasConCuatroAccionesCadaUna() throws Exception {
  var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
  int etapa=0;
  while(filas.find()) {
   assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
   var instrucciones=filas.group(4);
   assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),instrucciones,"e","r","p",null).acciones()).hasSize(4);
   var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(instrucciones);int accion=0;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
   assertThat(accion).isEqualTo(4);
  }
  assertThat(etapa).isEqualTo(8);
 }
}
