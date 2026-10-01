package escuela.alumno.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ActualizacionExpedienteInterfazTest {
    @Test
    void administracionConservaFiltrosEnExcelYPaginacion() throws Exception {
        String html = Files.readString(Path.of("src/main/resources/templates/admin/actualizaciones-expediente.html"));
        assertTrue(html.contains("/admin/actualizaciones-expediente/excel"));
        assertTrue(html.contains("estado=${filtro.estado}"));
        assertTrue(html.contains("desde=${filtro.desde}"));
        assertTrue(html.contains("pagina=${resultado.number+1}"));
    }

    @Test
    void portalSolicitaConsentimientoYAbreDocumentosEnOtraPestana() throws Exception {
        String documento = Files.readString(Path.of("src/main/resources/templates/portal/expediente-documento-form.html"));
        String medico = Files.readString(Path.of("src/main/resources/templates/portal/expediente-medico-form.html"));
        String listado = Files.readString(Path.of("src/main/resources/templates/portal/expediente.html"));
        assertTrue(documento.contains("*{consentimiento}"));
        assertTrue(medico.contains("*{consentimiento}"));
        assertTrue(documento.contains(".heic,.heif"));
        assertTrue(listado.contains("target=\"_blank\""));
    }

    @Test
    void migracionImpideAplicarUnaRevisionIncompleta() throws Exception {
        String sql = Files.readString(Path.of("src/main/resources/db/migration/V49__actualizacion_familiar_expedientes.sql"));
        assertTrue(sql.contains("ck_actualizacion_expediente_revision"));
        assertTrue(sql.contains("ux_actualizacion_medica_pendiente"));
        assertTrue(sql.contains("ACTUALIZACION_EXPEDIENTE_REVISAR"));
    }
}
