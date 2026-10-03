package escuela.admin.support;

import escuela.admin.dto.ModuloCatalogo;
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
                "Operación escolar · Control escolar",
                "Administración · Configuración escolar",
                "tituloArea.className = 'nav-area-title'",
                "const contieneActivo = Boolean(lista.querySelector('a.active'))",
                "localStorage.setItem(clavePreferencias");
        assertThat(estilos).contains(".nav-area-title", ".nav-section-toggle", ".nav-section-items[hidden]");
    }

    @Test
    void organizaLosModulosPorProcesoSinCambiarSusRutasTecnicas() {
        assertThat(ModuloCatalogo.ALUMNOS.seccion()).isEqualTo("Operación escolar · Control escolar");
        assertThat(ModuloCatalogo.MATERIAS.seccion()).isEqualTo("Operación escolar · Gestión académica");
        assertThat(ModuloCatalogo.CARGOS.seccion()).isEqualTo("Operación escolar · Cobranza escolar");
        assertThat(ModuloCatalogo.PAGOS.seccion()).isEqualTo("Administración · Finanzas");
        assertThat(ModuloCatalogo.INSTITUCIONES.seccion()).isEqualTo("Administración · Configuración escolar");
        assertThat(ModuloCatalogo.CARGOS.titulo()).isEqualTo("Adeudos de alumnos");
        assertThat(ModuloCatalogo.CARGOS.rutaListado()).isEqualTo("/admin/catalogos/cargos");
    }

    private String recurso(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
