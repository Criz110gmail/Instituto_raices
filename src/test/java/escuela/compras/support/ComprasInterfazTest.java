package escuela.compras.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ComprasInterfazTest {
    @Test
    void listadoConservaTodosLosFiltrosEnExcelYPaginacion() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/compras.html"));

        assertTrue(html.contains("/admin/compras/excel"));
        assertTrue(html.contains("proveedorId=${filtro.proveedorId}"));
        assertTrue(html.contains("cuentaId=${filtro.cuentaId}"));
        assertTrue(html.contains("estado=${filtro.estado}"));
        assertTrue(html.contains("desde=${filtro.desde}"));
        assertTrue(html.contains("pagina=${resultado.number+1}"));
    }

    @Test
    void formularioTienePartidasDinamicasYExplicaCuandoSeMueveElDinero() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/compra-form.html"));
        String js = Files.readString(Path.of("src/main/resources/static/js/compras.js"));
        String css = Files.readString(Path.of("src/main/resources/static/css/compras.css"));

        assertTrue(html.contains("data-add-line"));
        assertTrue(html.contains("data-line-template"));
        assertTrue(html.contains("Guardar no mueve dinero"));
        assertTrue(js.contains("data-grand-total"));
        assertTrue(css.contains("@media"));
    }

    @Test
    void detalleConfirmaEgresoYRevierteCancelacionSinBorrarHistorial() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/compra-detalle.html"));

        assertTrue(html.contains("/{id}/confirmar"));
        assertTrue(html.contains("Confirmar y registrar egreso"));
        assertTrue(html.contains("/{id}/cancelar"));
        assertTrue(html.contains("ingreso compensatorio"));
        assertTrue(html.contains("th:errors=\"*{motivo}\""));
    }

    @Test
    void migracionProtegeEstadosTotalesYTrazabilidadFinanciera() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V50__proveedores_y_compras.sql"));

        assertTrue(sql.contains("ck_compra_movimiento"));
        assertTrue(sql.contains("ck_compra_totales"));
        assertTrue(sql.contains("movimiento_egreso_id BIGINT UNIQUE"));
        assertTrue(sql.contains("reversion_financiera_id BIGINT UNIQUE"));
        assertTrue(sql.contains("COMPRA_CONFIRMAR"));
        assertTrue(sql.contains("COMPRA_CANCELAR"));
    }
}
