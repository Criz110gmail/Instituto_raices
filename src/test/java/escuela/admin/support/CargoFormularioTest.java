package escuela.admin.support;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CargoFormularioTest {
    @Test
    void formulariosDeCargoProcesanLaAccionParaIncluirCsrf() throws IOException {
        assertThat(plantilla("admin/cargo-form.html"))
                .contains("method=\"post\" th:action=\"@{/admin/cargos}\"")
                .contains("Esta descripción se conservará en el registro")
                .contains("Mes completo", "Fecha específica", "Rango personalizado")
                .contains("Fecha límite para pagar", "Así quedará registrado")
                .contains("Fecha de registro del cargo", "Registrar con una fecha diferente")
                .contains("Motivo del cambio de fecha", "readonly aria-readonly=\"true\"")
                .contains("/js/cargo-periodo.js")
                .doesNotContain(">Inicio del periodo *<", ">Fin del periodo *<")
                .contains("Registrar cargo");
        assertThat(plantilla("admin/cargo-detalle.html"))
                .contains("Motivo de fecha de registro diferente", "cargo.motivoFechaRegistroDiferente")
                .contains("Abonos vigentes", "cargo.montoPagado", "Falta por pagar");
        assertThat(plantilla("admin/cargo-generar.html"))
                .contains("Visualizar cuotas por aplicar")
                .contains("th:action=\"@{/admin/cargos/generar/vista-previa}\"")
                .contains("Confirmar y generar adeudos seleccionados")
                .contains("Importe original", "Beca por aplicar", "Total por cobrar",
                        "fila.montoBeca", "fila.descripcionBeca", "fila.importeNeto",
                        "vistaPrevia.becaTotal", "vistaPrevia.importeNetoTotal",
                        "No incluye ajustes manuales ni recargos posteriores")
                .contains("method=\"post\" th:action=\"@{/admin/cargos/generar}\"")
                .contains("data-preview-form", "data-generation-preview",
                        "/js/generation-preview.js");
    }

    private String plantilla(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream("templates/" + ruta)) {
            assertThat(entrada).as("plantilla %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
