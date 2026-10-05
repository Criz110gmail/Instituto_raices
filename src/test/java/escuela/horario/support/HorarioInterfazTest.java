package escuela.horario.support;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class HorarioInterfazTest {
    @Test void administracionConservaFiltrosExcelYAutocompletado()throws Exception{String h=Files.readString(Path.of("src/main/resources/templates/admin/horarios-clases.html"));String f=Files.readString(Path.of("src/main/resources/templates/admin/horario-clase-form.html"));assertTrue(h.contains("/admin/horarios-clases/excel"));assertTrue(h.contains("data-autocomplete"));assertTrue(h.contains("pagina=${resultado.number+1}"));assertTrue(h.contains("class=\"filters schedule-filters\""));assertTrue(f.contains("class=\"form-intro schedule-intro\""));assertTrue(f.contains("class=\"entity-form\""));assertTrue(f.contains("session-actions :: controls"));}
    @Test void portalesExponenHorarioResponsivo()throws Exception{String maestro=Files.readString(Path.of("src/main/resources/templates/maestros/horario.html"));String familia=Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));assertTrue(maestro.contains("teacher-schedule"));assertTrue(familia.contains("family-schedule-grid"));assertTrue(familia.contains("seccion=='HORARIO'"));}
}
