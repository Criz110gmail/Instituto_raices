package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaCancelarCondonacionContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V83__guia_cancelar_condonacion_total.sql"));
    }
    @Test void sieteEtapasConCuatroAccionesExplicitasCadaUna() throws Exception {
        var filas=Pattern.compile("(?m)^\\('cancelar-condonacion-total-500',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var texto=filas.group(5);
            assertThat(texto).startsWith("1. ").containsAnyOf("Desde el menú lateral","En la ventana privada");
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),texto,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
            var marcas=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(texto);int accion=0;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
        }
        assertThat(etapa).isEqualTo(7);
    }
    @Test void registraConfirmacionHumanaSinCancelarRegistrosRealesNiOtorgarRoles() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-08'","Cancelar y reactivar adeudos",
                "Confirmar cancelación del convenio","Con saldo pendiente","el mismo número de cargo",
                "el adeudo reapareció para pagar", "Saldo actual con B", "abonos previos", "no existen cargos nuevos",
                "version_contenido=version_contenido+1");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "INSERT INTO convenio_pago",
                "UPDATE cuenta_financiera", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
