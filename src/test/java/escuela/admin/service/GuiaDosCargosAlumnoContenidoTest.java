package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaDosCargosAlumnoContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V85__guia_dos_cargos_mismo_alumno.sql"));
    }
    @Test void ochoEtapasConCuatroAccionesYOrigenExplicito() throws Exception {
        var filas=Pattern.compile("(?m)^\\('dos-cargos-mismo-alumno-1000',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var texto=filas.group(5);
            assertThat(texto).startsWith("1. Desde el menú lateral");
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),texto,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
        }
        assertThat(etapa).isEqualTo(8);
    }
    @Test void documentaUnPagoSinLiquidarOtrosAdeudosNiModificarFinanzas() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-08'","INS-DOBLE-TEST","MENS-DOBLE-TEST",
                "Total recibido $1,000.00", "TEST-DOS-CARGOS-1000", "misma matrícula", "B + $1,000.00",
                "adeudo anterior de $400.00", "Confirmar y registrar como pendiente", "Validar y publicar",
                "Exportar Excel", "otra pestaña", "El propietario");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "INSERT INTO convenio_pago",
                "UPDATE cuenta_financiera", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
