package escuela.calendario.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CalendarioEscolarInterfazTest {
    @Test
    void listadoConservaFiltrosEnExcelYPaginacion() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/calendario-escolar.html"));
        assertTrue(html.contains("/admin/calendario-escolar/excel"));
        assertTrue(html.contains("nivelEducativoId=${filtro.nivelEducativoId}"));
        assertTrue(html.contains("tipo=${filtro.tipo}"));
        assertTrue(html.contains("pagina=${resultado.number+1}"));
    }

    @Test
    void formularioLimitaCatalogosPorInstitucionYEsResponsivo() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/calendario-escolar-form.html"));
        String js = Files.readString(Path.of("src/main/resources/static/js/calendario-escolar.js"));
        String css = Files.readString(Path.of("src/main/resources/static/css/calendario-escolar.css"));
        assertTrue(html.contains("data-calendar-institution"));
        assertTrue(html.contains("data-institution=${c.institucionId}"));
        assertTrue(js.contains("data-calendar-dependent"));
        assertTrue(css.contains("@media"));
    }
}
