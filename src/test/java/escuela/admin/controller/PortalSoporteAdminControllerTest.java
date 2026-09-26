package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PortalSoporteAdminControllerTest {
    @Test
    void soporteEsSoloLecturaYOcultaElReporteDeTransferencia() throws IOException {
        assertThat(Arrays.stream(PortalSoporteAdminController.class.getDeclaredMethods())
                .noneMatch(metodo -> metodo.isAnnotationPresent(PostMapping.class))).isTrue();

        String portal = plantilla("portal/inicio.html");
        assertThat(portal).contains("th:if=\"${soporte != true}\" href=\"/portal/pagos/reportar\"");
        assertThat(plantilla("portal/pago-form.html"))
                .doesNotContain("/admin/portal-soporte/")
                .contains("th:action=\"@{/portal/pagos/reportar}\"");
        assertThat(ModuloCatalogo.PORTAL_TUTOR.visibleCon(Set.of("PORTAL_TUTOR_SOPORTE"))).isTrue();
    }

    private String plantilla(String ruta) throws IOException {
        try (var entrada = getClass().getClassLoader().getResourceAsStream("templates/" + ruta)) {
            assertThat(entrada).as("plantilla %s", ruta).isNotNull();
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
