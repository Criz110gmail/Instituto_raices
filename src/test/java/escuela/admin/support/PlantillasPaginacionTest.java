package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class PlantillasPaginacionTest {

    @Test
    void lasListasDeTamaniosUsanSintaxisThymeleafValida() throws IOException {
        for (String plantilla : new String[]{
                "admin/reporte-tesoreria.html",
                "admin/estado-cuenta-alumno.html",
                "admin/cortes-caja.html",
                "admin/eventos-escolares.html"}) {
            String contenido;
            try (var entrada = getClass().getClassLoader()
                    .getResourceAsStream("templates/" + plantilla)) {
                assertThat(entrada).as("recurso %s", plantilla).isNotNull();
                contenido = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            }

            assertThat(contenido)
                    .as("sintaxis de paginación en %s", plantilla)
                    .doesNotContain("${{10,25,50,100}}")
                    .contains("${ {10, 25, 50, 100} }");
        }
    }
}
