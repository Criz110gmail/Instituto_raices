package escuela.personal;

import escuela.admin.dto.ModuloCatalogo;
import escuela.seguridad.service.ModuloPermiso;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class PrestamoProgramaEstructuraTest {
 @Test void entradaIndependienteNoAbreCatalogoGenerico(){
  assertThat(ModuloCatalogo.PRESTAMOS.rutaListado()).isEqualTo("/admin/prestamos");
  assertThat(ModuloCatalogo.PRESTAMOS.visibleCon(Set.of("PRESTAMO_LEER"))).isTrue();
  assertThat(ModuloCatalogo.PRESTAMOS.visibleCon(Set.of("EMPLEADO_LEER"))).isFalse();
 }
 @Test void consultarNoExpandeAEscrituraNiSalarios(){
  assertThat(ModuloPermiso.expandir(Set.of("PRESTAMO_LEER"))).containsExactly("PRESTAMO_LEER");
  assertThat(ModuloPermiso.expandir(Set.of("PRESTAMO_ADMINISTRAR"))).containsExactly("PRESTAMO_ADMINISTRAR");
  assertThat(java.util.Arrays.stream(ModuloPermiso.values()).filter(p->p.nombre().startsWith("Préstamos - ")).map(ModuloPermiso::nombre)).containsExactly("Préstamos - Consultar","Préstamos - Administrar");
 }
 @Test void guiaPendienteSinOperacionesNiGrants()throws Exception{
  var sql=Files.readString(Path.of("src/main/resources/db/migration/V155__guia_prestamos_programados.sql"));
  assertThat(sql).contains("'PENDIENTE'","porcentaje total","/admin/prestamos");
  assertThat(sql).doesNotContain("INSERT INTO rol_permiso","INSERT INTO movimiento_financiero","INSERT INTO prestamo_empleado");
 }
 @Test void editarCapturaNoAbreModalDeConfirmacion()throws Exception{
  var js=Files.readString(Path.of("src/main/resources/static/js/money-input.js"));
  assertThat(js).contains("'prestamo-programa','prestamo-entrega-programa'].includes(kind) && event.submitter?.hasAttribute('formaction')");
 }
 @Test void gestionUsaSubmenuAisladoSinCambiarTabsGlobales()throws Exception{
  var html=Files.readString(Path.of("src/main/resources/templates/admin/prestamo-programa-detalle.html"));
  assertThat(html).contains("data-prestamo-gestion-tab=\"borrador\"","data-prestamo-gestion-tab=\"entrega\"","data-prestamo-gestion-tab=\"cancelar\"","Entregar dinero al empleado");
  assertThat(html).doesNotContain("Entregar el principal");
  var js=Files.readString(Path.of("src/main/resources/static/js/prestamo-gestion.js"));
  assertThat(js).contains("aria-selected","ArrowLeft","ArrowRight","activate(root.dataset.activeSection)").doesNotContain("fetch(","submit(");
 }
}
