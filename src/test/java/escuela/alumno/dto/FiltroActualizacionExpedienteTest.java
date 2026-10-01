package escuela.alumno.dto;

import escuela.alumno.entity.EstadoActualizacionExpediente;
import escuela.alumno.entity.TipoActualizacionExpediente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FiltroActualizacionExpedienteTest {
    @Test
    void normalizaPaginacionYConservaCriterios() {
        LocalDate desde = LocalDate.of(2026, 10, 1);
        LocalDate hasta = LocalDate.of(2026, 10, 31);
        var filtro = new FiltroActualizacionExpediente(1L, 2L,
                TipoActualizacionExpediente.FICHA_MEDICA, EstadoActualizacionExpediente.ENVIADA,
                desde, hasta, "  familia Mendoza  ", -4, 999).normalizado();

        assertEquals(0, filtro.pagina());
        assertEquals(25, filtro.tamanio());
        assertEquals("familia Mendoza", filtro.texto());
        assertEquals(2L, filtro.alumnoId());
        assertEquals(desde, filtro.desde());
        assertEquals(hasta, filtro.hasta());
    }
}
