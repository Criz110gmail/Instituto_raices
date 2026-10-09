package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaExcedenteContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V86__guia_devolucion_excedente.sql"));
    }
    @Test void ochoEtapasConCuatroAccionesYOrigenExplicito() throws Exception {
        var filas=Pattern.compile("(?m)^\\('pago-excedente-400-devolucion-100',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
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
    @Test void excedenteNoReabreDeudaNiLiquidaOtrosCargosYSqlSoloEditorial() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-08'","EXC-TEST", "TEST-EXCEDENTE-400",
                "Capturar un total recibido diferente", "B + $400.00", "B + $300.00",
                "Disponible $100.00", "deuda que se recuperará $0.00", "Aplicado $300.00, Devuelto $100.00 y Disponible $0.00",
                "El propietario confirmó", "Tipo de devolución Parcial", "Ejecutar devolución y publicar egreso",
                "no liquida automáticamente otras deudas", "PAGO_DEVOLVER", "Exportar Excel", "otra pestaña");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "INSERT INTO convenio_pago",
                "UPDATE cuenta_financiera", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
