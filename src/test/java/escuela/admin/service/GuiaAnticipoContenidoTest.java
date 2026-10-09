package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaAnticipoContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V90__guia_pago_anticipado_beca.sql"));
    }
    @Test void diezEtapasOrdenadasConCuatroAccionesYModulosExplicitos() throws Exception {
        var filas=Pattern.compile("(?m)^\\('pago-anticipado-beca-porcentaje',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
            assertThat(filas.group(4)).isNotBlank();
        }
        assertThat(etapa).isEqualTo(10);
    }
    @Test void confirmaSoloCasoProbadoYNoModificaFinanzasNiRoles() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","ANTICIPO-TEST-01","Conservar beca y agregar beneficio",
            "Porcentaje adicional","Beca conservada200","Beneficio adicional80","Pago requerido720",
            "Guardar propuesta","Registrar pago completo","Validar y publicar","BECA200","DESCUENTO80",
            "19730","18290","/familias","beca20% aún activa","otra pestaña",
            "cancelar o devolver es un escenario separado aún no confirmado");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "UPDATE cuenta_financiera",
            "INSERT INTO aplicacion_pago", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
}
