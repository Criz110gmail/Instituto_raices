package escuela.personal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GuiaPrestamoNetoCeroContenidoTest {
    @Test void documentaSoloLaAceptacionHumanaSinOperacionesNiPermisos() throws Exception {
        var sql = Files.readString(Path.of("src/main/resources/db/migration/V159__guia_prestamo_neto_cero_confirmado.sql"));
        assertThat(sql).contains("prestamos-programados-nomina", "DATE '2026-11-06'",
                "version_contenido=version_contenido+1", "PRE-000002", "EMP-PREST-CERO-01",
                "EXTRA-RED-TEST", "NOV-2026-CIERRE-TEST", "$315.11", "$174.29", "$1,150.00",
                "LIQUIDADO", "sin NPI", "egreso original", "no tiene cancelación",
                "todas las exportaciones", "prueba manual de rol sólo consulta sigue pendiente");
        var actualizaciones = Pattern.compile("(?i)UPDATE\\s+(\\w+)").matcher(sql)
                .results().map(m -> m.group(1)).toList();
        var inserciones = Pattern.compile("(?i)INSERT\\s+INTO\\s+(\\w+)").matcher(sql)
                .results().map(m -> m.group(1)).toList();
        assertThat(actualizaciones).containsExactly("guia_proceso", "guia_proceso_paso");
        assertThat(inserciones).containsExactly("guia_proceso_paso");
        assertThat(sql).doesNotContain("DELETE FROM", "ALTER TABLE", "CREATE TABLE",
                "UPDATE prestamo", "UPDATE cuenta_financiera", "INSERT INTO nomina", "rol_permiso");
        var pasos = Pattern.compile("(?m)^\\((\\d+),'").matcher(sql).results()
                .map(m -> Integer.parseInt(m.group(1))).toList();
        assertThat(pasos).containsExactly(9, 10, 11, 12, 13);
    }
}
