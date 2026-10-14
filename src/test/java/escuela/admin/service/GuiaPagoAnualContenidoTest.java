package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaPagoAnualContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V94__guia_pago_anual_diciembre_bonificado.sql"));
    }
    @Test void doceEtapasConModulosRutasYCuatroAccionesExplicitas() throws Exception {
        var filas=Pattern.compile("(?m)^\\('pago-anual-diciembre-bonificado',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int numero=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++numero);
            var paso=new GuiaProcesosService.Paso(numero,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
            assertThat(filas.group(4)).isNotBlank();
            assertThat(filas.group(5)).contains("1. ","2. ","3. ","4. ");
        }
        assertThat(numero).isEqualTo(12);
    }
    @Test void registraResultadoConfirmadoSinModificarFinanzasNiInventarBecaOMetodo() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-13'","$12,000.00","$11,000.00","$1,000.00",
            "Anual liquidado","12/12 meses","once meses pagados completos","Bonificada, sin abono",
            "No falsear vigencias","15/06/2027","Validar y publicar","Guardar propuesta",
            "no repitas","/familias","otra pestaña","no se atribuye un método específico",
            "pruebas de duplicados/dos pestañas siguen pendientes","consulta adicional disponible");
        var inserts=Pattern.compile("INSERT INTO (\\w+)").matcher(sql());
        assertThat(inserts.results().map(m->m.group(1)).toList()).containsExactly("guia_proceso","guia_proceso_permiso","guia_proceso_paso");
        assertThat(sql()).doesNotContain("UPDATE ","DELETE FROM","ALTER TABLE","rol_permiso","INSERT INTO aplicacion_pago");
    }
}
