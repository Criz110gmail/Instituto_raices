package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CobranzaAsistidaInterfazTest {
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
                "monto.value = solicitado.toFixed(2)");
    }

    private String recurso(String ruta) throws Exception {
        return Files.readString(Path.of("src/main/resources/" + ruta));
    }
}
