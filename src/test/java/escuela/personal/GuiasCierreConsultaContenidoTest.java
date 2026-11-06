package escuela.personal;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GuiasCierreConsultaContenidoTest {
    @Test void documentaLosDosCierresSinModificarOperacionesNiAsignarDerechos() throws Exception {
        var sql=Files.readString(Path.of("src/main/resources/db/migration/V160__guias_consulta_prestamos_cheques_confirmadas.sql"));
        assertThat(sql).contains("consulta-cheques-cuenta-confirmada", "prestamos-programados-nomina",
                "No tienes acceso a esta información", "Préstamos - Consultar", "DATE '2026-11-06'",
                "CONFIRMADA", "$315.11", "$174.29", "$1,150.00", "NPI-17", "NPI-18",
                "no acredita un ensayo separado de lector de cheques", "no devuelve automáticamente");
        var tablas=Pattern.compile("(?i)(?:UPDATE|INSERT\\s+INTO)\\s+(\\w+)").matcher(sql)
                .results().map(x->x.group(1)).toList();
        assertThat(tablas).containsOnly("guia_proceso","guia_proceso_paso","guia_proceso_permiso");
        assertThat(sql).doesNotContain("DELETE FROM", "ALTER TABLE", "CREATE TABLE", "INSERT INTO rol", "UPDATE usuario", "UPDATE cheque", "UPDATE prestamo");
    }
}
