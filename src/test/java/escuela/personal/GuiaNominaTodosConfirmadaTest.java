package escuela.personal;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

class GuiaNominaTodosConfirmadaTest {
 @Test void guiaUsaRutaRealEvidenciaConfirmadaYSinRepetirPagos() throws Exception {
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V150__guia_nomina_todos_elegibles_cero_confirmada.sql"));
  assertThat(sql).contains("nomina-todos-elegibles-cero-confirmada","CONFIRMADA","2026-11-03","BOR-000014","NPI-14","NPI-15","NPI-16","$1,267.74","$1,195.11","Todos los elegibles del periodo","Preparar pago colectivo","Neto cero confirmado","Exportar revisión a Excel","no repetir");
  assertThat(Pattern.compile("(?m)^\\([1-8],").matcher(sql).results().count()).isEqualTo(8);
  assertThat(sql).doesNotContain("UPDATE cuenta_financiera","INSERT INTO nomina_pago_intento","UPDATE nomina_borrador","INSERT INTO rol_permiso");
 }
}
