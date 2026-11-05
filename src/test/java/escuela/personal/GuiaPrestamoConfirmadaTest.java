package escuela.personal;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

class GuiaPrestamoConfirmadaTest {
 @Test void confirmaSoloElCasoHumanoYConservaLosFoliosSinMoverDinero()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V158__guia_prestamos_tomas_confirmada.sql"));
  assertThat(sql).contains("estado='CONFIRMADA'","DATE '2026-11-05'","PRE-000001","BOR-000017","BOR-000018","NPI-19 cancelado","NPI-20 pagado","NPI-21 pagado","$415.11","$174.29","$1,150.00","sin interés nuevo","neto cero","prueba manual independiente");
  var tablas=Pattern.compile("(?i)UPDATE\\s+(\\w+)").matcher(sql).results().map(x->x.group(1)).toList();
  assertThat(tablas).containsExactly("guia_proceso","guia_proceso_paso");
  assertThat(sql).doesNotContain("INSERT INTO","DELETE FROM","CREATE TABLE","ALTER TABLE","UPDATE prestamo","UPDATE cuenta_financiera","UPDATE rol");
 }
}
