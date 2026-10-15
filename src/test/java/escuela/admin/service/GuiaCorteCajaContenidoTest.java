package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaCorteCajaContenidoTest {
    String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V97__guia_corte_caja_sin_diferencia.sql"));}
    @Test void diezEtapasConCuatroAccionesYModulosExplicitos()throws Exception {
        var filas=Pattern.compile("(?m)^\\('corte-caja-sin-diferencia',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int n=0;while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++n);
            assertThat(filas.group(4)).isNotBlank();
            assertThat(new GuiaProcesosService.Paso(n,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null).acciones()).hasSize(4);
            assertThat(filas.group(5)).startsWith("1. ").containsAnyOf("Administración → Finanzas","En el detalle","En el modal");
        }
        assertThat(n).isEqualTo(10);
    }
    @Test void conservaResultadoHumanoConteoCiegoYSoloEscriturasEditoriales()throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-15'","Saldo actual $1,150","Sin diferencia · $0.00","Folios1 a2","Confirmar y cerrar caja","no muestra el saldo esperado","Cancelar dentro del modal","no significa que la cuenta quedó sin dinero","La prueba de faltante1","ni asigna permisos","No hay pagos de alumnos");
        var escrituras=Pattern.compile("(?:INSERT INTO|UPDATE) (\\w+)").matcher(sql());
        assertThat(escrituras.results().map(m->m.group(1)).toList()).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(sql()).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso");
        String[] permisos={"CUENTA_FINANCIERA_LEER","CUENTA_FINANCIERA_ADMINISTRAR","MOTIVO_FINANCIERO_LEER","MOTIVO_FINANCIERO_ADMINISTRAR","MOVIMIENTO_FINANCIERO_LEER","MOVIMIENTO_FINANCIERO_REGISTRAR","CORTE_CAJA_LEER","CORTE_CAJA_ADMINISTRAR"};
        assertThat(sql()).contains(permisos);
    }
}
