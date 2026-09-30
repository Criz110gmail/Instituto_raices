package escuela.docente.controller;

import escuela.docente.dto.MaestroResponse;
import escuela.docente.entity.EstadoPlaneacion;
import escuela.docente.service.FotografiaMaestroService;
import escuela.docente.service.PdfPlaneacionService;
import escuela.docente.service.PlaneacionService;
import escuela.docente.service.PortalAlumnoMaestroService;
import escuela.seguridad.service.UsuarioPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortalMaestroControllerTest {

    @Mock private PlaneacionService service;
    @Mock private PdfPlaneacionService pdf;
    @Mock private PortalAlumnoMaestroService alumnos;
    @Mock private FotografiaMaestroService fotografias;
    @InjectMocks private PortalMaestroController controller;

    @Test
    void conservaFiltrosYPaginaLaConsultaDocente() {
        UsuarioPrincipal principal = mock(UsuarioPrincipal.class);
        MaestroResponse perfil = mock(MaestroResponse.class);
        when(perfil.id()).thenReturn(7L);
        when(service.perfil(principal)).thenReturn(perfil);
        LocalDate desde = LocalDate.of(2026, 9, 1);
        LocalDate hasta = LocalDate.of(2026, 9, 30);
        when(service.listarMaestro(principal, desde, hasta, EstadoPlaneacion.PUBLICADA, 2))
                .thenReturn(Page.empty());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.inicio(desde, hasta, EstadoPlaneacion.PUBLICADA, 2,
                principal, model);

        assertThat(vista).isEqualTo("maestros/inicio");
        assertThat(model).containsEntry("desde", desde).containsEntry("hasta", hasta)
                .containsEntry("estado", EstadoPlaneacion.PUBLICADA).containsKey("estados");
        verify(service).listarMaestro(principal, desde, hasta, EstadoPlaneacion.PUBLICADA, 2);
    }

    @Test
    void explicaCuandoElRangoDeFechasEstaInvertido() {
        UsuarioPrincipal principal = mock(UsuarioPrincipal.class);
        MaestroResponse perfil = mock(MaestroResponse.class);
        when(perfil.id()).thenReturn(7L);
        when(service.perfil(principal)).thenReturn(perfil);
        LocalDate desde = LocalDate.of(2026, 9, 30);
        LocalDate hasta = LocalDate.of(2026, 9, 1);
        when(service.listarMaestro(principal, desde, hasta, null, 0)).thenReturn(Page.empty());
        ExtendedModelMap model = new ExtendedModelMap();

        controller.inicio(desde, hasta, null, 0, principal, model);

        assertThat(model.get("errorFiltro")).asString().contains("fecha final");
    }
}
