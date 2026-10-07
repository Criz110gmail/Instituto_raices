package escuela.admin.service;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;
class GuiaRechazoReenvioContenidoTest {
    String sql() throws Exception {return Files.readString(Path.of("src/main/resources/db/migration/V64__guia_rechazo_reenvio_pago.sql"));}
    @Test void diezEtapasNumeradasEmpiezanEnMenuOPortalYNoConfundenMontos() throws Exception {
        var patron=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$,\\$texto\\$(.*?)\\$texto\\$");
        var matcher=patron.matcher(sql());int esperado=1;
        while(matcher.find()) {
            int numero=Integer.parseInt(matcher.group(1));assertThat(numero).isEqualTo(esperado++);
            String instrucciones=matcher.group(5);
            var paso=new GuiaProcesosService.Paso(numero,matcher.group(2),matcher.group(3),matcher.group(4),instrucciones,"Ejemplo","Resultado","Precaución",null);
            assertThat(paso.acciones()).hasSizeGreaterThanOrEqualTo(4);
            assertThat(instrucciones).containsAnyOf("Desde el menú lateral","desde el menú lateral","desde el inicio","/familias","Regresa a administración");
            var marcas=Pattern.compile("(?<!\\S)(\\d+)\\. ").matcher(instrucciones);int accion=1;
            while(marcas.find())assertThat(Integer.parseInt(marcas.group(1))).as("etapa"+numero).isEqualTo(accion++);
        }
        assertThat(esperado).isEqualTo(11);
    }
    @Test void conservaRechazoMotivoDosReportesYUnSoloIngreso() throws Exception {
        var s=sql();assertThat(s).contains("pago-rechazado-reenvio-400","DATE '2026-10-07'","'CONFIRMADA'",
                "TEST-RECHAZO-400","TEST-CORREGIDO-400","Rechazar sin afectar saldos",
                "Ver motivo del rechazo","Motivo registrado por administración","Enviar transferencia a revisión",
                "Validar y publicar","Ver comprobante PDF","no B +800", "no implica pagar otra vez",
                "PAGO_VALIDAR","CONCEPTO_COBRO_ADMINISTRAR","CUOTA_ALUMNO_ADMINISTRAR");
        var inserts=Pattern.compile("(?i)INSERT INTO ([a-z_]+)").matcher(s);
        assertThat(inserts.results().map(m->m.group(1)).toList()).containsExactly("guia_proceso","guia_proceso_permiso","guia_proceso_paso");
        assertThat(s).doesNotContain("UPDATE pago", "UPDATE cargo", "INSERT INTO rol_permiso", "ALTER TABLE");
    }
    @Test void enlacesSoloAdministrativosYBotonesCorrespondenALasPantallas() throws Exception {
        var s=sql();assertThat(s).contains("NULL", "/admin/catalogos/pagos", "/admin/cargos/generar")
                .doesNotContain("$texto$/familias$texto$");
        var pago=Files.readString(Path.of("src/main/resources/templates/admin/pago-detalle.html"));
        assertThat(pago).contains("Rechazar sin afectar saldos","Validar y publicar");
        var portal=Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));
        assertThat(portal).contains("Ver motivo del rechazo","Ver comprobante PDF","Buscar pagos");
    }
}
