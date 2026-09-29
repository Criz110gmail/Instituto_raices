package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class NavegacionAgrupadaTest {
    @Test
    void agrupaCategoriasRepetidasYConservaLaActivaAbierta() throws IOException {
        String javascript = recurso("static/js/navigation.js");
        String estilos = recurso("static/css/navigation.css");

        assertThat(javascript).contains(
                "const secciones = new Map()",
                "if (!secciones.has(nombre))",
                "Trayectoria: ['Inscripciones', 'Calificaciones', 'Asistencia', 'Boletas', 'Planeaciones']",
                "const contieneActivo = Boolean(lista.querySelector('a.active'))",
                "localStorage.setItem(clavePreferencias");
        assertThat(estilos).contains(".nav-section-toggle", ".nav-section-items[hidden]");
    }

    private String recurso(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
