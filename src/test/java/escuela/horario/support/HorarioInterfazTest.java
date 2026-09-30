package escuela.horario.support;

import org.junit.jupiter.api.Test;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

class HorarioInterfazTest {
    @Test void administracionConservaFiltrosExcelYAutocompletado()throws Exception{String h=Files.readString(Path.of("src/main/resources/templates/admin/horarios-clases.html"));assertTrue(h.contains("/admin/horarios-clases/excel"));assertTrue(h.contains("data-autocomplete"));assertTrue(h.contains("pagina=${resultado.number+1}"));}
    @Test void portalesExponenHorarioResponsivo()throws Exception{String maestro=Files.readString(Path.of("src/main/resources/templates/maestros/horario.html"));String familia=Files.readString(Path.of("src/main/resources/templates/portal/seccion.html"));assertTrue(maestro.contains("teacher-schedule"));assertTrue(familia.contains("family-schedule-grid"));assertTrue(familia.contains("seccion=='HORARIO'"));}
}
