package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class TesoreriaHistoricaGuiaTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V102__guia_tesoreria_corte_historico_confirmado.sql"));
    }
    @Test void documentaAceptacionAcotadaSinOperacionesFinancieras() throws Exception {
        var texto=sql();
        assertThat(texto).contains("reportes-cuentas-tesoreria","estado='CONFIRMADA'","BBVA_COBROS",
                "01/10/2026","15/10/2026","16/10/2026","$1,440.00","$1,000.00","$500.00","$1,500.00",
                "No confirma aún el corte histórico del estado del alumno","version_contenido=version_contenido+1");
        var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
        assertThat(tablas).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso","INSERT INTO guia_proceso(");
    }
    @Test void agregaEtapasSieteYOchoConCuatroAccionesCadaUna() throws Exception {
        var filas=Pattern.compile("(?m)^\\(([78]),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=6;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),filas.group(4),"e","r","p",null);
            assertThat(paso.acciones()).hasSize(4);
            var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(filas.group(4));int accion=0;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
            assertThat(accion).isEqualTo(4);
        }
        assertThat(etapa).isEqualTo(8);
    }
}
