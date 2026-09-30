package escuela.calendario.dto;

import escuela.calendario.entity.TipoFechaCalendario;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FiltroCalendarioEscolarTest {
    @Test
    void normalizaPaginacionYConservaTodosLosCriterios() {
        LocalDate desde = LocalDate.of(2026, 9, 1);
        LocalDate hasta = LocalDate.of(2026, 9, 30);
        var filtro = new FiltroCalendarioEscolar(1L, 2L, 3L, 4L,
                TipoFechaCalendario.EVENTO_ACADEMICO, true, desde, hasta,
                "  consejo técnico  ", -2, 999).normalizado();

        assertEquals(0, filtro.pagina());
        assertEquals(25, filtro.tamanio());
        assertEquals("consejo técnico", filtro.texto());
        assertEquals(3L, filtro.plantelId());
        assertEquals(4L, filtro.nivelEducativoId());
        assertEquals(desde, filtro.desde());
        assertEquals(hasta, filtro.hasta());
    }
}
