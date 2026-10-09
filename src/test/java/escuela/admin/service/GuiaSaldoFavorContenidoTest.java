package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaSaldoFavorContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V88__guia_saldo_favor_hermanos.sql"));
    }
    @Test void diezEtapasConCuatroAccionesExplicitas() throws Exception {
        var filas=Pattern.compile("(?m)^\\('saldo-favor-hermanos-reversion',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var texto=filas.group(5);
            assertThat(texto).startsWith("1. Desde el menú lateral");
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),texto,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
        }
        assertThat(etapa).isEqualTo(10);
    }
    @Test void documentaFamiliaValidacionHermanosReversionYReaplicacionSinMutarFinanzas() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-09'","SF-TEST-20261009","TEST-SALDO-FAVOR-400",
            "El importe recibido es mayor al reportado","Saldo a favor de tu familia","Gestionar saldo a favor e historial",
            "Deshacer esta aplicación","Aplicaciones a adeudos","Reversiones de aplicaciones","Todas las operaciones",
            "Cancelar","Confirmar operación","PAGO_CANCELAR","B + $400.00","no B + $500.00",
            "aplicar $100.00 − revertir $100.00 + reaplicar $100.00","Exportar Excel","otra pestaña",
            "NO entrar a Ejecutar devolución","El propietario confirmó el cierre completo");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "UPDATE cuenta_financiera", "INSERT INTO aplicacion_pago",
            "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
