package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.docente.dto.MaestroResponse;
import escuela.docente.entity.EstadoPlaneacion;
import escuela.docente.service.*;
import escuela.horario.service.HorarioClaseService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.web.bind.annotation.PostMapping;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PortalMaestroSoporteAdminControllerTest {
    @Test void reutilizaPortalConPrincipalDocenteYConservaFiltros() {
        var soporte = mock(SoportePortalMaestroService.class);
        var planes = mock(PlaneacionService.class);
        var controller = new PortalMaestroSoporteAdminController(soporte, planes,
                mock(PortalAlumnoMaestroService.class), mock(FotografiaMaestroService.class),
                mock(HorarioClaseService.class), mock(PdfPlaneacionService.class));
        var admin = mock(UsuarioPrincipal.class); var docente = mock(UsuarioPrincipal.class);
        when(soporte.contexto(admin, 7L, "PLANEACIONES")).thenReturn(docente);
        var perfil = mock(MaestroResponse.class); when(perfil.id()).thenReturn(7L);
        when(planes.perfil(docente)).thenReturn(perfil);
        var desde = LocalDate.of(2026, 10, 1); var hasta = LocalDate.of(2026, 10, 31);
        when(planes.listarMaestro(docente, desde, hasta, EstadoPlaneacion.PUBLICADA, 2)).thenReturn(Page.empty());
        var model = new ExtendedModelMap();
        assertThat(controller.inicio(7L, desde, hasta, EstadoPlaneacion.PUBLICADA, 2, admin, model)).isEqualTo("maestros/inicio");
        assertThat(model).containsEntry("soporte", true).containsEntry("rutaPortal", "/admin/portal-maestros-soporte/7")
                .containsEntry("desde", desde).containsEntry("hasta", hasta);
        verify(planes).listarMaestro(docente, desde, hasta, EstadoPlaneacion.PUBLICADA, 2);
        verify(planes, never()).listarMaestro(eq(admin), any(), any(), any(), anyInt());
    }

    @Test void moduloRequierePermisoPropioYNoTieneOperacionesDeEscritura() throws Exception {
        assertThat(Arrays.stream(PortalMaestroSoporteAdminController.class.getDeclaredMethods())
                .noneMatch(m -> m.isAnnotationPresent(PostMapping.class))).isTrue();
        assertThat(ModuloCatalogo.PORTAL_MAESTRO.visibleCon(Set.of("PORTAL_MAESTRO_SOPORTE"))).isTrue();
        assertThat(ModuloCatalogo.PORTAL_MAESTRO.visibleCon(Set.of("MAESTRO_ADMINISTRAR", "PORTAL_TUTOR_SOPORTE"))).isFalse();
        assertThat(ModuloCatalogo.PORTAL_MAESTRO.seccion()).isEqualTo("Administración · Seguridad y soporte");
        String captura = Files.readString(Path.of("src/main/resources/templates/maestros/planeacion-form.html"));
        assertThat(captura).contains("th:disabled=\"${soporte == true}\"", "data-readonly", "th:unless=\"${soporte == true}\"");
        String inicio = Files.readString(Path.of("src/main/resources/templates/maestros/inicio.html"));
        assertThat(inicio).contains("soporte != true and", "${rutaPortal}", "pagina=${planeaciones.number+1}");
    }
}
