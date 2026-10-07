package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaDevolucionDescuentoContenidoTest {
    String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V68__guia_devolucion_descuento.sql"));
    }
    @Test void nueveEtapasConRecorridosYAccionesNumeradas() throws Exception {
        var filas = Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int esperado = 1;
        while (filas.find()) {
            int numero = Integer.parseInt(filas.group(1));
            assertThat(numero).isEqualTo(esperado++);
            String instrucciones = filas.group(5);
            var paso = new GuiaProcesosService.Paso(numero, filas.group(2), filas.group(3), filas.group(4), instrucciones, "Ejemplo", "Resultado", "Precaución", null);
            assertThat(paso.acciones()).hasSizeGreaterThanOrEqualTo(4);
            assertThat(instrucciones).containsAnyOf("Desde el menú lateral", "Abre una ventana privada separada");
            var acciones = Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);
            int orden = 1;
            while (acciones.find()) assertThat(Integer.parseInt(acciones.group(1))).isEqualTo(orden++);
        }
        assertThat(esperado).isEqualTo(10);
    }
    @Test void documentaSaldoFondosYAlternativasSinModificarOperaciones() throws Exception {
        String s = sql();
        assertThat(s).contains("'CONFIRMADA'", "DATE '2026-10-07'", "DEV-DESC-TEST", "TEST-DEV-DESC-100",
                "total ajustado $300.00", "pagado acumulado $300.00", "saldo $0.00",
                "el descuento no mueve fondos", "Confirmar operación", "Volver a revisar",
                "pestaña Ajustar saldo", "Historial", "AJUSTE_CARGO_ADMINISTRAR", "No volver a pagar");
        var inserts = Pattern.compile("(?i)INSERT INTO ([a-z_]+)").matcher(s);
        assertThat(inserts.results().map(m -> m.group(1)).toList())
                .containsExactly("guia_proceso", "guia_proceso_permiso", "guia_proceso_paso");
        var updates = Pattern.compile("(?i)UPDATE ([a-z_]+)").matcher(s);
        assertThat(updates.results().map(m -> m.group(1)).toList())
                .containsExactly("guia_proceso_paso", "guia_proceso");
        assertThat(s).doesNotContain("ALTER TABLE", "rol_permiso", "$texto$/familias$texto$");
    }
    @Test void botonesYPestaniasExistenEnLasVistas() throws Exception {
        String cargo = Files.readString(Path.of("src/main/resources/templates/admin/cargo-detalle.html"));
        String pago = Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        assertThat(cargo).contains("Ajustar saldo", "Aplicar ajuste al saldo", "Así quedaría el cargo", "Saldo después del ajuste", "Historial");
        assertThat(pago).contains("Devolver desde otra cuenta", "Devolución parcial", "Ejecutar devolución y publicar egreso");
    }
}
