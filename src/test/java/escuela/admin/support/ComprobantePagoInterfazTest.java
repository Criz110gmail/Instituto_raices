package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class ComprobantePagoInterfazTest {
    @Test
    void comprobantesSeAbrenEnOtraPestanaDesdeAdministracionYPortal() throws IOException {
        assertThat(recurso("templates/admin/pago-detalle.html"))
                .contains("/{id}/comprobante-pago")
                .contains("target=\"_blank\"")
                .contains("Comprobante oficial de pago");
        assertThat(recurso("templates/portal/seccion.html"))
                .contains("/comprobante-pago")
                .contains("target=\"_blank\"")
                .contains("Ver comprobante PDF");
    }

    @Test
    void moduloUsaNombreClaroSinCambiarSusRutasInternas() throws IOException {
        assertThat(recurso("templates/admin/cargo-form.html"))
                .contains("Volver a pagos de alumnos", "@{/admin/cargos}");
        assertThat(recurso("templates/admin/cargo-generar.html"))
                .contains("Generar pagos programados", "/admin/cargos/generar");
    }

    private String recurso(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream(ruta)) {
            assertThat(entrada).as("recurso %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
