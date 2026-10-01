package escuela.compras.dto;

import escuela.compras.entity.EstadoCompra;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FiltroCompraTest {
    @Test
    void normalizaPaginacionYConservaTodosLosCriterios() {
        LocalDate desde = LocalDate.of(2026, 9, 1);
        LocalDate hasta = LocalDate.of(2026, 9, 30);
        var filtro = new FiltroCompra(1L, 2L, 3L, 4L, EstadoCompra.CONFIRMADA,
                desde, hasta, "  factura A-1542  ", -3, 999).normalizado();

        assertEquals(0, filtro.pagina());
        assertEquals(25, filtro.tamanio());
        assertEquals("factura A-1542", filtro.texto());
        assertEquals(2L, filtro.plantelId());
        assertEquals(3L, filtro.proveedorId());
        assertEquals(4L, filtro.cuentaId());
        assertEquals(EstadoCompra.CONFIRMADA, filtro.estado());
        assertEquals(desde, filtro.desde());
        assertEquals(hasta, filtro.hasta());
    }
}
