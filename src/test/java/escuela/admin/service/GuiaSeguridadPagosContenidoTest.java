package escuela.admin.service;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class GuiaSeguridadPagosContenidoTest {
    String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V96__guia_seguridad_registro_validacion_pagos.sql"));}
    @Test void doceEtapasDeCuatroAccionesConModulosExplicitos()throws Exception {
        var filas=Pattern.compile("(?m)^\\('seguridad-registro-validacion-pagos',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int n=0;while(filas.find()){
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++n);
            assertThat(new GuiaProcesosService.Paso(n,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null).acciones()).hasSize(4);
            assertThat(filas.group(4)).isNotBlank();
        }assertThat(n).isEqualTo(12);
    }
    @Test void documentaTresConfirmacionesSinOperarFinanzas()throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-15'","DUP-TEST-02","S+200, nunca S+400","Este pago ya estaba validado",
            "No se realizó ningún movimiento adicional.","SIN RECARGAR","Cancelar DENTRO del modal","no se seleccione dos veces","No se conoce su folio");
        var escrituras=Pattern.compile("(?:INSERT INTO|UPDATE) (\\w+)").matcher(sql());
        assertThat(escrituras.results().map(m->m.group(1)).toList()).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(sql()).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso");
    }
}
