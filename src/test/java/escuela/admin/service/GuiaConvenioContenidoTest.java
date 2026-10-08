package escuela.admin.service;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

class GuiaConvenioContenidoTest {
    String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V76__guia_convenio_dos_hijos.sql"));
    }
    @Test void diezEtapasDetalladasConOrigenYAccionesConsecutivas() throws Exception {
        var filas = Pattern.compile("(?m)^\\('convenio-dos-hijos-800',(\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa = 0;
        while (filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            String instrucciones = filas.group(5);
            assertThat(instrucciones).startsWith("1. ").containsAnyOf("Desde el menú lateral", "Abre una ventana privada separada");
            var paso = new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),instrucciones,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSize(4);
            var marcas = Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);
            int accion = 0;
            while (marcas.find()) assertThat(Integer.parseInt(marcas.group(1))).isEqualTo(++accion);
        }
        assertThat(etapa).isEqualTo(10);
    }
    @Test void casoConfirmadoSoloEditorialSinEjecutarOperacionesNiOtorgarRoles() throws Exception {
        assertThat(sql()).contains("'CONFIRMADA'", "DATE '2026-10-08'", "CONVENIO_PAGO_ADMINISTRAR",
                "$480.00/$320.00", "B+$800.00", "Cumplido", "CONV-ORIG-TEST", "CONV-ACUERDO-TEST",
                "Confirmar y enviar a revisión", "Cancelar conserva todo sin enviar", "Ver comprobante PDF",
                "No canceles este convenio", "otro caso independiente");
        assertThat(sql()).doesNotContain("INSERT INTO pago", "UPDATE cargo", "INSERT INTO convenio_pago",
                "UPDATE cuenta_financiera", "ALTER TABLE", "rol_permiso", "DELETE FROM");
    }
    @Test void soloVerDePagosUsaBotonComun() throws Exception {
        var html = Files.readString(Path.of("src/main/resources/templates/admin/catalogo.html"));
        assertThat(html).contains("th:class=\"${resultado.modulo.name() == 'PAGOS' ? 'new-button' : 'edit-link'}\"",
                "resultado.modulo.rutaMantenimiento() + '/' + fila.id + '/editar'");
    }
}
