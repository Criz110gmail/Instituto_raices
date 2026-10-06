package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CobranzaAsistidaInterfazTest {
    @Test
    void cuotaSeparaFechaLimiteMesesYVigenciaAvanzada() throws Exception {
        String html = recurso("templates/admin/cuota-alumno-form.html");
        assertThat(html).contains("Fecha límite de pago", "Primer mes que se cobrará",
                "Último mes que se cobrará", "<details", "Configuración adicional",
                "cuota-resumen", "/js/cuota-alumno-form.js");
        assertThat(html).doesNotContain("Fin de cobro", "Vencimiento exacto");
        assertThat(recurso("static/js/cuota-alumno-form.js")).contains(
                "No se repetirá en los siguientes meses", "vigencia-inscripcion", "respuesta.redirected");
    }
    @Test
    void inscripcionConectaCuotaCargoYPagoSinMezclarLosFormularios() throws Exception {
        String inscripcion = recurso("templates/admin/inscripcion-form.html");
        String cuota = recurso("templates/admin/cuota-alumno-form.html");
        String pago = recurso("templates/admin/pago-form.html");

        assertThat(inscripcion).contains("data-student-tab=\"cobranza\"",
                "/admin/cuotas-alumno/nuevo", "generar-unico", "/admin/pagos/nuevo");
        assertThat(cuota).contains("*{retornoInscripcionId}", "*{generarCargoAhora}",
                "La inscripción ya está seleccionada");
        assertThat(pago).contains("Pago rápido desde cobranza", "*{retornoInscripcionId}");
    }

    @Test
    void seleccionarCargoPrecargaSuSaldoEnLaDistribucionYElTotal() throws Exception {
        String javascript = recurso("static/js/pago-form.js");

        assertThat(javascript).contains("opcion.monto != null",
                "importe.value = Number(opcion.monto).toFixed(2)",
                "sincronizarTotalConDistribucion()");
    }

    @Test
    void pagoParcialSincronizaElTotalYExplicaElSaldoQueConservaraElCargo() throws Exception {
        String javascript = recurso("static/js/pago-form.js");

        assertThat(javascript).contains("sincronizarTotalConDistribucion()",
                "monto.value = distribuido > 0 ? distribuido.toFixed(2) : ''",
                "el cargo conservará un saldo estimado de");
    }

    private String recurso(String ruta) throws Exception {
        return Files.readString(Path.of("src/main/resources/" + ruta));
    }
}
