package escuela.admin.service;

import java.nio.file.*;
import java.util.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiasReportesFinancierosContenidoTest {
    private String sql() throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V100__guias_reportes_financieros_confirmados.sql"));}
    @Test void cuatroGuiasConDieciochoEtapasYCuatrosAccionesOrdenadas() throws Exception {
        var filas=Pattern.compile("(?m)^\\('([^']+)',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        Map<String,Integer> etapas=new LinkedHashMap<>();
        while(filas.find()) {
            int numero=etapas.merge(filas.group(1),1,Integer::sum);assertThat(Integer.parseInt(filas.group(2))).isEqualTo(numero);
            var instrucciones=filas.group(5);
            assertThat(new GuiaProcesosService.Paso(numero,filas.group(3),"Administración",filas.group(4),instrucciones,"e","r","p",null).acciones()).hasSize(4);
            var marcas=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);int accion=0;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
        }
        assertThat(etapas).containsExactlyInAnyOrderEntriesOf(Map.of("reportes-cuentas-tesoreria",6,"reporte-alumno-filtros",5,"reporte-concentrado-agrupaciones",4,"reporte-concentrado-excel",3));
    }
    @Test void aceptacionHumanaAcotadaYExcelNuevoPendienteSinEscriturasOperativas() throws Exception {
        var texto=sql();assertThat(texto).contains("'PENDIENTE'","'CONFIRMADA'","REPORTE_FINANCIERO_CONSULTAR","01/10/2026","16/10/2026","$1,940.00","$1,440.00","$1,500.00","$2,100.00","$1,600.00","Excel no existía durante esa aceptación","No repetir","sin movimientos");
        var tablas=Pattern.compile("(?:INSERT INTO|UPDATE) (\\w+)").matcher(texto);
        assertThat(tablas.results().map(m->m.group(1)).toList()).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso","INSERT INTO pago","UPDATE cargo");
    }
}
