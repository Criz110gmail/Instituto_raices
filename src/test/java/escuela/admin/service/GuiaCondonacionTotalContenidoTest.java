package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaCondonacionTotalContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V82__guia_condonacion_total_convenio.sql"));
    }
    @Test void nueveEtapasDetalladasConOrigenYCuatroAccionesCadaUna() throws Exception {
        var filas=Pattern.compile("(?m)^\\('convenio-condonacion-total-500',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var instrucciones=filas.group(5);
            assertThat(instrucciones).startsWith("1. ").containsAnyOf("Desde el menú lateral","desde Operación escolar","formulario de Convenios","En la ventana privada");
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),instrucciones,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
            var acciones=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);int accion=0;
            while(acciones.find()) assertThat(Integer.parseInt(acciones.group(1))).isEqualTo(++accion);
        }
        assertThat(etapa).isEqualTo(9);
    }
    @Test void documentaCasoConfirmadoSinOperacionesNiReversionConfirmada() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'","DATE '2026-10-08'","CONVENIO_PAGO_ADMINISTRAR",
                "Condonado totalmente","Monto por pagar $0.00","Confirmar y condonar saldo",
                "Incluido en convenio","Con saldo pendiente","el saldo bancario no cambió",
                "no generar", "saldo real", "reversión que aún no se probó");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "INSERT INTO convenio_pago",
                "UPDATE cuenta_financiera", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
    @Test void botonesDePagosMantienenTamanoNaturalYEstilosComunes() throws Exception {
        var html=Files.readString(Path.of("src/main/resources/templates/admin/catalogo.html"));
        var css=Files.readString(Path.of("src/main/resources/static/css/catalogo-rangos.css"));
        assertThat(html).contains("class=\"catalogo-filter-actions\" th:if=\"${resultado.modulo.name() == 'PAGOS' or resultado.modulo.name() == 'CUOTAS_ALUMNO' or resultado.modulo.name() == 'CARGOS'}\"");
        assertThat(css).contains(".filters .catalogo-filter-actions>button{flex:0 0 auto;width:auto;min-width:0}", "flex-wrap:wrap");
    }
}
