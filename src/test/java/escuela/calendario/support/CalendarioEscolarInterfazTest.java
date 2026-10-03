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
        assertTrue(html.contains("calendar-filter-grid"));
        assertTrue(html.contains("calendar-results-heading"));
        assertTrue(html.contains("form-error calendar-error"));
    }

    @Test
    void formularioLimitaCatalogosPorInstitucionYEsResponsivo() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/calendario-escolar-form.html"));
        String js = Files.readString(Path.of("src/main/resources/static/js/calendario-escolar.js"));
        String css = Files.readString(Path.of("src/main/resources/static/css/calendario-escolar.css"));
        String ayuda = Files.readString(Path.of("src/main/resources/static/js/contextual-help.js"));
        assertTrue(html.contains("data-calendar-institution"));
        assertTrue(html.contains("data-institution=${c.institucionId}"));
        assertTrue(html.contains("form-top"));
        assertTrue(html.contains("session-actions :: controls"));
        assertTrue(html.contains("entity-form calendar-entity-form"));
        assertTrue(html.contains("calendar-scope-guide"));
        assertTrue(js.contains("data-calendar-dependent"));
        assertTrue(css.contains("@media"));
        assertTrue(css.contains("[data-theme=dark]"));
        assertTrue(ayuda.contains("'Calendario escolar'"));
    }
}
