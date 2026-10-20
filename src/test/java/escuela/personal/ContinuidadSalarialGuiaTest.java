package escuela.personal;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;
class ContinuidadSalarialGuiaTest {
 @Test void actualizaSoloReferenciaSinDarPorProbadosLosSueldos()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V117__referencia_guia_condiciones_salariales.sql"));
  assertThat(Pattern.compile("(?m)^UPDATE (\\w+)").matcher(sql).results().map(m->m.group(1)).toList()).containsExactly("guia_proceso_paso","guia_proceso");
  assertThat(sql).contains("permiso salarial propio","Su prueba humana es independiente y sigue pendiente","no genera nómina ni mueve dinero");
  assertThat(sql).doesNotContain("INSERT INTO","UPDATE empleado","UPDATE condicion_salarial","DELETE FROM");
 }
}
