package escuela.admin.service;

import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CortesHistoricosAlumnoCobranzaGuiaTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V103__guias_cortes_historicos_alumno_cobranza.sql"));
    }
    @Test void documentaAmbasAceptacionesSinOperacionesFinancieras() throws Exception {
        var texto=sql();
        assertThat(texto).contains("estado='CONFIRMADA'","ALUM-2026-002","Todos autorizados",
                "15/10/2026","10/10/2026","20/11/2026","20/12/2026","$1,600.00","$800.00",
                "ambas hojas del Excel","Concepto de cobro","No implica aceptación humana de corte histórico por plantel/ciclo");
        var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
        assertThat(tablas).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso","INSERT INTO guia_proceso(");
    }
    @Test void agregaCuatroEtapasContiguasConCuatroAccionesCadaUna() throws Exception {
        var filas=Pattern.compile("(?m)^\\('([^']+)',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        Map<String,List<Integer>> etapas=new LinkedHashMap<>();
        while(filas.find()) {
            int numero=Integer.parseInt(filas.group(2));etapas.computeIfAbsent(filas.group(1),k->new ArrayList<>()).add(numero);
            var paso=new GuiaProcesosService.Paso(numero,filas.group(3),"Administración",filas.group(4),filas.group(5),"e","r","p",null);
            assertThat(paso.acciones()).hasSize(4);
            var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(filas.group(5));int accion=0;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
            assertThat(accion).isEqualTo(4);
        }
        assertThat(etapas).containsExactlyInAnyOrderEntriesOf(Map.of(
                "reporte-alumno-filtros",List.of(6,7),"reporte-concentrado-agrupaciones",List.of(5,6)));
    }
}
