package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CierreTraspasoGuiaTest {
 private String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V109__cerrar_pruebas_traspaso_historico_y_bloqueo.sql"));}
 @Test void cierraSoloEvidenciaConfirmadaSinDuplicarGuiasNiModificarDinero() throws Exception {
  var texto=sql();
  assertThat(texto).contains("'CONFIRMADA'","TRASPASO-TEST-001","$4,650.00","$906.00","$406.00","$5,150.00",
   "BANCO-TRASPASO-TEST","entrada300/salida0/MXN","captura completa","sin formulario","Esta operación ya fue revertida",
   "No es posible publicar una segunda reversa","/admin/reversiones/transferencias/1/nueva",
   "revisión de sesiones pendiente por separado","no equivale a una prueba manual de POST forzado",
   "version_contenido=version_contenido+1","AND numero=7","AND numero=6");
  var tablas=Pattern.compile("(?im)^UPDATE (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
  assertThat(tablas).hasSize(3).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
  assertThat(texto).doesNotContain("INSERT INTO","DELETE FROM","ALTER TABLE","UPDATE cuenta_financiera","UPDATE rol_permiso");
 }
 @Test void actualizacionesDePasosMantienenCuatroAccionesOrdenadas() throws Exception {
  var pasos=Pattern.compile("(?m)^instrucciones=\\$texto\\$(.*?)\\$texto\\$").matcher(sql());int total=0;
  while(pasos.find()) {
   assertThat(new GuiaProcesosService.Paso(1,"Cierre","Administración","Guía",pasos.group(1),"e","r","p",null).acciones()).hasSize(4);
   var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(pasos.group(1));int accion=0;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
   assertThat(accion).isEqualTo(4);total++;
  }
  assertThat(total).isEqualTo(2);
 }
}
