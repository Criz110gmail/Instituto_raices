package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaDevolucionNuevoPagoContenidoTest {
    String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V65__guia_devolucion_parcial_nuevo_pago.sql"));
    }
    @Test void ochoEtapasDetalladasSinConfundirImportesConNumeracion() throws Exception {
        var patron = Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$");
        var matcher = patron.matcher(sql()); int esperado = 1;
        while (matcher.find()) {
            int numero = Integer.parseInt(matcher.group(1));
            assertThat(numero).isEqualTo(esperado++);
            String instrucciones = matcher.group(5);
            var paso = new GuiaProcesosService.Paso(numero, matcher.group(2), matcher.group(3),
                    matcher.group(4), instrucciones, "Ejemplo", "Resultado", "Precaución", null);
            assertThat(paso.acciones()).hasSizeGreaterThanOrEqualTo(4);
            assertThat(instrucciones).containsAnyOf("Desde el menú lateral", "desde el inicio", "Desde el inicio", "/familias");
            var acciones = Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones); int orden = 1;
            while (acciones.find()) assertThat(Integer.parseInt(acciones.group(1))).isEqualTo(orden++);
        }
        assertThat(esperado).isEqualTo(9);
    }
    @Test void soloDocumentaCasoConfirmadoConPermisosYSinOperacionesReales() throws Exception {
        String s = sql();
        assertThat(s).contains("'CONFIRMADA'", "DATE '2026-10-07'", "PAGO_DEVOLVER", "TEST-REPAGO-100",
                "TEST-DEV-PARCIAL-100", "S - $100 + $100 = S", "saldo $0.00", "conserva su importe original $400.00",
                "alternativa devolución más descuento manual se prueba por separado");
        var inserts = Pattern.compile("(?i)INSERT INTO ([a-z_]+)").matcher(s);
        assertThat(inserts.results().map(m -> m.group(1)).toList())
                .containsExactly("guia_proceso", "guia_proceso_permiso", "guia_proceso_paso");
        assertThat(s).doesNotContain("UPDATE pago", "UPDATE cargo", "INSERT INTO rol_permiso", "ALTER TABLE", "$texto$/familias$texto$");
    }
    @Test void botonesCoincidenConPantallasDeDevolucionYValidacion() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        assertThat(html).contains("Devolver desde otra cuenta", "Dinero a devolver", "Abonos de los que se devolverá dinero",
                "Así quedaría la devolución", "Ejecutar devolución y publicar egreso", "Validar y publicar");
        assertThat(sql()).contains("/admin/catalogos/pagos", "/admin/catalogos/cuentas-financieras", "NULL");
    }
}
