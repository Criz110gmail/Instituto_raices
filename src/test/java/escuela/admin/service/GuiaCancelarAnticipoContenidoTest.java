package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.assertThat;

class GuiaCancelarAnticipoContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V91__guia_cancelacion_pago_anticipado.sql"));
    }

    @Test void doceEtapasOrdenadasConCuatroAccionesYModulos() throws Exception {
        var filas=Pattern.compile("(?m)^\\('cancelar-pago-anticipado-beca',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
            assertThat(filas.group(4)).isNotBlank();
            var numeros=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(filas.group(5));
            int accion=0;
            while(numeros.find())assertThat(Integer.parseInt(numeros.group(1))).isEqualTo(++accion);
        }
        assertThat(etapa).isEqualTo(12);
    }

    @Test void documentaResultadoConfirmadoSinModificarDineroNiOtorgarRoles() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","ANTICIPO-TEST-02","PAG-20261009-000002",
            "ANT-20261010-000002","Beneficio al validar el pago completo80","Cancelar y conservar trazabilidad",
            "Confirmar cancelación","fecha de apertura","un único registro por1440",
            "deuda total1600","banco B","BECA200 conservada","No reversar manualmente",
            "PAGO_CANCELAR","devolución sigue pendiente","version_contenido=version_contenido+1");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "UPDATE cuenta_financiera",
            "INSERT INTO aplicacion_pago", "INSERT INTO ajuste_cargo", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
