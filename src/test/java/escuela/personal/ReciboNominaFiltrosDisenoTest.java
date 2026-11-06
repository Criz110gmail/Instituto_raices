package escuela.personal;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ReciboNominaFiltrosDisenoTest {
    private String recurso(String ruta)throws Exception {
        try(var in=getClass().getClassLoader().getResourceAsStream(ruta)) {
            return new String(in.readAllBytes(),StandardCharsets.UTF_8);
        }
    }
    @Test void ambosAutocompletadosPermitenOpcionesMultilineaSinAlturaFija()throws Exception {
        var html=recurso("templates/admin/recibos-nomina.html");
        assertThat(html).contains("recibos-filters","/admin/recibos-nomina/empleados/buscar","/admin/recibos-nomina/periodos/buscar");
        var css=recurso("static/css/nomina.css");
        assertThat(css.substring(css.indexOf(".recibos-filters"))).contains("height:auto!important","grid-column:auto","grid-template-columns:minmax(0,1fr)","white-space:normal","overflow-wrap:anywhere","position:static","line-height:1.6");
    }
}
