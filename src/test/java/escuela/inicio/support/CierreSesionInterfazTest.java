package escuela.inicio.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CierreSesionInterfazTest {

    @Test
    void losPortalesMuestranCerrarSesionYConservanSuOrigen() throws IOException {
        String familias = recurso("/templates/portal/inicio.html");
        String maestros = recurso("/templates/maestros/fragments/cabecera.html");
        assertThat(recurso("/templates/maestros/inicio.html"))
                .contains("maestros/fragments/cabecera :: header");

        assertThat(familias)
                .contains("Cerrar sesión")
                .contains("name=\"origen\" value=\"familias\"")
                .contains("th:action=\"@{/logout}\"");
        assertThat(maestros)
                .contains("Cerrar sesión")
                .contains("name=\"origen\" value=\"maestros\"")
                .contains("th:action=\"@{/logout}\"");
    }

    private String recurso(String ruta) throws IOException {
        try (var entrada = getClass().getResourceAsStream(ruta)) {
            assertThat(entrada).as("recurso %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
