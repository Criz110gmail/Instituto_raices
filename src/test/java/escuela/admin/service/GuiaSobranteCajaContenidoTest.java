package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class GuiaSobranteCajaContenidoTest {
    private String sql()throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V99__guia_sobrante_caja_justificado.sql"));
    }

    @Test void ochoEtapasOrdenadasConCuatroAcciones()throws Exception {
        var filas=Pattern.compile("(?m)^\\('corte-caja-sobrante-1',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            assertThat(filas.group(4)).isNotBlank();
            assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null).acciones()).hasSize(4);
            var numeros=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(filas.group(5));int accion=0;
            while(numeros.find())assertThat(Integer.parseInt(numeros.group(1))).isEqualTo(++accion);
        }
        assertThat(etapa).isEqualTo(8);
    }

    @Test void registraAceptacionHumanaSinOperacionesFinancierasNiRoles()throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-16'","CAJA-SOBRANTE-TEST",
            "Explica la diferencia entre el efectivo declarado y el saldo esperado",
            "Estado ABIERTO","Diferencia = $1,151 - $1,150 = +$1.00","Movimientos incluidos0",
            "sin ingreso automático de $1","saldo inicial original es1150",
            "usuario de recuperación","hasta diez cajas","Confirmar y abrir corte",
            "Confirmar y cerrar caja","No repetir","controles adicionales recomendados",
            "reportes financieros siguen pendientes","version_contenido=version_contenido+1");
        var escrituras=Pattern.compile("(?:INSERT INTO|UPDATE) (\\w+)").matcher(sql());
        assertThat(escrituras.results().map(m->m.group(1)).toList()).isNotEmpty()
            .allSatisfy(tabla->assertThat(tabla).startsWith("guia_proceso"));
        assertThat(sql()).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso");
    }
}
