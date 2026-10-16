package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class ConfirmacionExcelConcentradoGuiaTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V101__confirmacion_excel_concentrado_cobranza.sql"));
    }
    @Test void confirmaSoloElCasoReportadoSinEscriturasFinancierasNiPermisos() throws Exception {
        var texto=sql();
        assertThat(texto).contains("estado='CONFIRMADA'","reporte-concentrado-excel","Todos autorizados",
                "Concepto de cobro","Ciclo Escolar 2026-2027","Plantel parian","$2,100.00","$500.00","$1,600.00",
                "16/10/2026","No confirma todavía reconstrucción","version_contenido=version_contenido+1");
        var tablas=Pattern.compile("(?im)^UPDATE (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
        assertThat(tablas).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(texto).doesNotContain("INSERT INTO","DELETE FROM","ALTER TABLE","rol_permiso");
    }
    @Test void conservaTresEtapasConCuatroAccionesCadaUna() throws Exception {
        var filas=Pattern.compile("(?m)^\\(([123]),\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var instrucciones=filas.group(2);
            var paso=new GuiaProcesosService.Paso(etapa,"t","Administración","Concentrado",instrucciones,"e","r","p",null);
            assertThat(paso.acciones()).hasSize(4);
            var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(instrucciones);int accion=0;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
            assertThat(accion).isEqualTo(4);
        }
        assertThat(etapa).isEqualTo(3);
    }
}
