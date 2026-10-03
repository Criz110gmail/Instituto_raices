package escuela.admin.support;
import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.assertj.core.api.Assertions.assertThat;
class ConvenioPagoInterfazTest {
 @Test void permiteBuscarCargosSinCargarCatalogosMasivos()throws Exception{String html=Files.readString(Path.of("src/main/resources/templates/admin/convenio-pago-form.html"));String js=Files.readString(Path.of("src/main/resources/static/js/convenio-pago-form.js"));assertThat(html).contains("cargo-busqueda","cargoIds");assertThat(js).contains("URLSearchParams","/admin/convenios-pago/cargos-disponibles");}
}
