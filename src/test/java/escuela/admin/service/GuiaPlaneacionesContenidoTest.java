package escuela.admin.service;

import java.nio.file.*;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class GuiaPlaneacionesContenidoTest {
    private String sql() throws Exception {
        return Files.readString(Path.of("src/main/resources/db/migration/V112__guia_planeaciones_docentes.sql"));
    }

    @Test void soloDocumentacionConPermisosSinOtorgarAccesos() throws Exception {
        var texto=sql();
        var tablas=Pattern.compile("(?im)^INSERT INTO (\\w+)").matcher(texto)
                .results().map(m->m.group(1)).toList();
        assertThat(tablas).containsExactly("guia_proceso","guia_proceso_permiso","guia_proceso_paso");
        assertThat(texto).contains("p.codigo='PLANEACION_LEER'","'CONFIRMADA'","DATE '2026-10-19'");
        assertThat(texto).doesNotContain("UPDATE ","DELETE FROM","ALTER TABLE","rol_permiso",
                "PORTAL_MAESTRO_ACCEDER","INSERT INTO planeacion ");
    }

    @Test void diezEtapasOrdenadasConActoresRutasYCuatroAcciones() throws Exception {
        var filas=Pattern.compile("(?m)^\\((\\d+),\\$texto\\$(.*?)\\$texto\\$,'(.*?)',\\$texto\\$(.*?)\\$texto\\$,\\R\\$texto\\$(.*?)\\$texto\\$").matcher(sql());
        int etapa=0;
        while(filas.find()) {
            assertThat(Integer.parseInt(filas.group(1))).isEqualTo(++etapa);
            var paso=new GuiaProcesosService.Paso(etapa,filas.group(2),filas.group(3),filas.group(4),filas.group(5),"e","r","p",null);
            assertThat(paso.acciones()).hasSize(4);
            assertThat(paso.perfil()).isIn("Maestro","Administración","Administración y maestro","Maestro y administración");
        }
        assertThat(etapa).isEqualTo(10);
        assertThat(sql()).contains("/admin/planeaciones","/maestros/acceso","Mis planeaciones",
                "Correcciones solicitadas por administración","Confirmar guardado","Confirmar envío",
                "Confirmar solicitud","Confirmar revisión","Confirmar publicación","Confirmar reapertura",
                "Ver PDF · Versión 1 — Publicación inicial","Ver PDF · Versión 2 — Nueva publicación",
                "dd/MM/yyyy · HH:mm","sin descarga automática","Cancelar","Historial",
                "Descarte, concurrencia, solapamientos y aislamiento entre maestros siguen pendientes");
    }
}
