package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiasCancelacionesContenidoTest {
 String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V74__guias_cancelaciones_cobranza.sql"));}
 @Test void diecisieteEtapasNumeradasConOrigenExplicito() throws Exception {
  var filas=Pattern.compile("(?m)^\\('([^']+)',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
  var numeros=new LinkedHashMap<String,List<Integer>>();int total=0;
  while(filas.find()) {
   total++;String slug=filas.group(1);int numero=Integer.parseInt(filas.group(2));
   numeros.computeIfAbsent(slug,k->new ArrayList<>()).add(numero);
   String instrucciones=filas.group(6);
   var paso=new GuiaProcesosService.Paso(numero,filas.group(3),filas.group(4),filas.group(5),instrucciones,"Ejemplo","Resultado","Precaución",null);
   assertThat(paso.acciones()).hasSizeGreaterThanOrEqualTo(4);
   assertThat(instrucciones).startsWith("1. ").containsAnyOf("Desde el menú lateral", "Abre una ventana privada separada");
   var marcas=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);int esperado=1;
   while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).as(slug+" etapa"+numero).isEqualTo(esperado++);
  }
  assertThat(total).isEqualTo(17);
  assertThat(numeros.get("cancelar-pago-pendiente-600")).containsExactly(1,2,3,4,5,6,7);
  assertThat(numeros.get("cancelar-pago-validado-600")).containsExactly(1,2,3,4,5);
  assertThat(numeros.get("cancelar-adeudo-sin-abonos-600")).containsExactly(1,2,3,4,5);
 }
 @Test void soloContenidoConfirmadoSinOperacionesNiPermisosDeRol() throws Exception {
  String s=sql();
  assertThat(s).contains("'CONFIRMADA'", "DATE '2026-10-07'", "PAGO_CANCELAR", "PAGO_VALIDAR", "CARGO_ADMINISTRAR",
      "TEST-CANCELACION", "B+$600", "Abonos vigentes $0.00", "Falta por pagar $600.00", "Falta por pagar $0.00",
      "no habilita su regeneración automática", "Cancelar el pago no cancela el adeudo");
  var inserts=Pattern.compile("(?i)INSERT INTO ([a-z_]+)").matcher(s);
  assertThat(inserts.results().map(m->m.group(1)).toList()).containsExactly("guia_proceso","guia_proceso_permiso","guia_proceso_paso");
  assertThat(s).doesNotContain("UPDATE pago", "UPDATE cargo", "rol_permiso", "ALTER TABLE", "$texto$/familias$texto$");
 }
 @Test void botonesYModalesCoincidenConLasPantallasActuales() throws Exception {
  String pago=Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
  String cargo=Files.readString(Path.of("src/main/resources/templates/admin/cargo-detalle.html"));
  String js=Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
  assertThat(pago).contains("Cancelar y conservar trazabilidad", "Validar y publicar", "Cancelar pago registrado por error");
  assertThat(cargo).contains("Cancelar cargo al alumno", "Otras gestiones opcionales", "Abonos vigentes", "Falta por pagar");
  for(String boton:List.of("Confirmar cancelación", "Confirmar cancelación del adeudo", "Confirmar validación", "Confirmar y registrar como pendiente", "Confirmar y crear cuota")) {
   assertThat(js).contains(boton);assertThat(sql()).contains(boton);
  }
 }
}
