package escuela.admin.service;
import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaDevolucionAnticipoContenidoTest {
    String sql()throws Exception{return Files.readString(Path.of("src/main/resources/db/migration/V95__guia_devoluciones_acuerdo_anticipado.sql"));}
    @Test void diezEtapasDeCuatroAccionesConModulosExplicitos()throws Exception{
        var filas=Pattern.compile("(?m)^\\('devolver-acuerdo-anticipado',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int n=0;while(filas.find()){
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++n);
            assertThat(new GuiaProcesosService.Paso(n,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null).acciones()).hasSize(4);
            assertThat(filas.group(4)).isNotBlank();
        }assertThat(n).isEqualTo(10);
    }
    @Test void documentaAmbosResultadosConfirmadosYTocaSoloContenidoEditorial()throws Exception{
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-14'","ANTICIPO-TEST-05","abono600/deuda100","600+700=1300","100+1300=1400",
            "saldo800","beca200 conservada","No uses el pago anual","Concepto de cobro","/familias","dos pestañas","tres ensayos pendientes");
        var operaciones=Pattern.compile("(?:INSERT INTO|UPDATE) (\\w+)").matcher(sql());
        assertThat(operaciones.results().map(m->m.group(1)).toList()).allSatisfy(t->assertThat(t).startsWith("guia_proceso"));
        assertThat(sql()).doesNotContain("DELETE FROM","ALTER TABLE","rol_permiso");
    }
}
