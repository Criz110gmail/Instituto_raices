package escuela.personal;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

class GuiaNominaCeroDirectoConfirmadaTest {
 @Test void documentaCierreConfirmadoSinReproducirOperaciones() throws Exception {
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V151__guia_nomina_cero_directo_confirmada.sql"));
  assertThat(sql).contains("nomina-cero-directo-confirmada","CONFIRMADA","BOR-000015","$1,195.11","$274.29","$1,150.00","Revisar neto cero","Editar justificación","Cancelar dentro del modal","/admin/preparacion-nomina/pago-individual","sin NPI ni egreso","no repetir");
  assertThat(Pattern.compile("(?m)^\\([1-6],").matcher(sql).results().count()).isEqualTo(6);
  assertThat(sql).doesNotContain("UPDATE cuenta_financiera","INSERT INTO nomina_pago_intento","INSERT INTO nomina_cierre_cero","UPDATE nomina_borrador","INSERT INTO rol_permiso");
 }
}
