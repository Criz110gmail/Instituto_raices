package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaCondonacionAbonoContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V84__guia_condonacion_con_abono_previo.sql"));
    }
    @Test void nueveEtapasConCuatroAccionesYOrigenExplicito() throws Exception {
        var filas=Pattern.compile("(?m)^\\('condonacion-abono-previo-100-400',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var texto=filas.group(5);
            assertThat(texto).startsWith("1. Desde el menú lateral");
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),texto,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
        }
        assertThat(etapa).isEqualTo(9);
    }
    @Test void conservaAbonoBancoYDatosHistoricosSinEscriturasFinancieras() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-08'","Tarjeta","Registrar pago parcial",
                "Confirmar y registrar como pendiente","Validar y publicar","Confirmar cancelación del convenio",
                "Dinero pendiente de asignar", "B + $100.00", "importes históricos", "comprobante",
                "pagado acumulado $100.00 y saldo por pagar $400.00", "Exportar Excel",
                "no representan una condonación vigente", "version_contenido=version_contenido+1");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "INSERT INTO convenio_pago",
                "UPDATE cuenta_financiera", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
