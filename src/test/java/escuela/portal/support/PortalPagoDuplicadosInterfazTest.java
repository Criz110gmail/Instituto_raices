package escuela.portal.support;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.*;
class PortalPagoDuplicadosInterfazTest {
 @Test void evitaDuplicadosEnAyudaClickYEnvioSinRetirarProteccionServidor()throws Exception{
  String js=Files.readString(Path.of("src/main/resources/static/js/portal-pago.js"));
  assertThat(js).contains("cargoEnOtraFila", "datos.resultados.filter", "if (cargoEnOtraFila(opcion.id, fila))",
    "Este adeudo ya está agregado al pago", "form?.addEventListener('submit'", "evento.preventDefault()");
  String service=Files.readString(Path.of("src/main/java/escuela/portal/service/PortalPagoService.java"));
  assertThat(service).contains("if (!seleccionados.add(solicitud.getCargoId()))", "Un cargo sólo puede seleccionarse una vez");
 }
}
