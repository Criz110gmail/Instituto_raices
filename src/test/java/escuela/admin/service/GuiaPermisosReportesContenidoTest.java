package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaPermisosReportesContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V104__guia_permisos_alcance_reportes_confirmados.sql"));
    }
    @Test void aceptacionAcotadaSinConcederPermisosNiCrearRegistrosOperativos() throws Exception {
        var texto=sql();
        assertThat(texto).contains("'CONFIRMADA'","reportes-permisos-alcance","prueba.sinreportes","prueba.reportes.parian",
                "institucionId=1&plantelId=2","USUARIO_ADMINISTRAR","ROL_ADMINISTRAR","REPORTE_FINANCIERO_CONSULTAR",
                "$2,100.00","$500.00","$1,600.00","Segundo plantel vacío","no contaron como prueba de alcance",
                "no crea usuarios, roles, planteles ni movimientos");
        var tablas=Pattern.compile("(?im)^(?:UPDATE|INSERT INTO) (\\w+)").matcher(texto).results().map(m->m.group(1)).toList();
        assertThat(tablas).isNotEmpty().allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(texto).doesNotContain("DELETE FROM","ALTER TABLE","INSERT INTO rol_permiso","INSERT INTO usuario","UPDATE pago");
    }
    @Test void nueveEtapasContiguasConCuatroAccionesNumeradas() throws Exception {
        var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var instrucciones=filas.group(4);
            assertThat(new GuiaProcesosService.Paso(etapa,filas.group(2),"Administración",filas.group(3),instrucciones,"e","r","p",null).acciones()).hasSize(4);
            var marcas=Pattern.compile("(?<!\\S)([1-4])\\. ").matcher(instrucciones);int accion=0;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
            assertThat(accion).isEqualTo(4);
        }
        assertThat(etapa).isEqualTo(9);
    }
}
