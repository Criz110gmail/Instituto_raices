package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiasVariantesAnticipoContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V92__guias_variantes_pago_anticipado.sql"));
    }
    @Test void tresGuiasIndependientesConDiezEtapasYCuatroAccionesCadaUna() throws Exception {
        for(String slug:new String[]{"anticipo-mensualidad-bonificada","anticipo-sustituir-beca","anticipo-beneficio-fijo"}) {
            var filas=Pattern.compile("(?m)^\\('"+slug+"',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
            int etapa=0;
            while(filas.find()) {
                assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
                var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null);
                assertThat(paso.acciones()).hasSize(4);
                assertThat(filas.group(4)).isNotBlank();
            }
            assertThat(etapa).as(slug).isEqualTo(10);
        }
    }
    @Test void documentaLosTresCalculosBecaComprobanteYEstadosSinOperarFinanzas() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-13'","ANTICIPO-TEST-03","ANTICIPO-TEST-04","ANTICIPO-TEST-05",
                "TEST-MENSUALIDAD-GRATIS-800","TEST-SUSTITUCION-1800","TEST-FIJO-1400",
                "1000-200-800=0","1000-200+200-100=900","pago700+700=1400",
                "Bonificada, sin abono","No se crea aplicación0 ficticia","CORRECCION/AUMENTO",
                "Guardar propuesta","Registrar pago completo","Validar y publicar","otra pestaña","/familias",
                "devolución parcial100 y restante1300, aún pendientes de confirmación");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "UPDATE cuenta_financiera",
                "INSERT INTO aplicacion_pago", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
