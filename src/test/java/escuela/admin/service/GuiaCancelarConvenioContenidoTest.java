package escuela.admin.service;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaCancelarConvenioContenidoTest {
 String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V79__guia_cancelar_convenio_sin_pagos.sql"));}
 @Test void ochoEtapasConMenusYAccionesNumeradas() throws Exception {
  var filas=Pattern.compile("(?m)^\\('cancelar-convenio-sin-pagos-500',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
  int etapa=0;
  while(filas.find()) {
   assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
   String instrucciones=filas.group(5);
   assertThat(instrucciones).startsWith("1. Desde el menú lateral");
   var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),instrucciones,"Ejemplo","Resultado","Precaución",null);
   assertThat(paso.acciones()).hasSizeGreaterThanOrEqualTo(4);
   var marcas=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);int accion=0;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
  }
  assertThat(etapa).isEqualTo(8);
 }
 @Test void confirmacionHumanaYContenidoSinOperacionesFinancieras() throws Exception {
  assertThat(sql()).contains("'CONFIRMADA'", "DATE '2026-10-08'", "CONVENIO_PAGO_ADMINISTRAR",
   "Confirmar cancelación del convenio", "conserva el motivo", "originales $300.00/$200.00",
   "no se perdona la deuda", "no reportar transferencias", "bancoB");
  assertThat(sql()).doesNotContain("UPDATE cargo", "INSERT INTO pago", "UPDATE convenio_pago",
   "INSERT INTO convenio_pago", "ALTER TABLE", "DELETE FROM", "rol_permiso");
 }
}
