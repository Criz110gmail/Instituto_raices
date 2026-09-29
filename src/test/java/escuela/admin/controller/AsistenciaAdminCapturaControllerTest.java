package escuela.admin.controller;

import escuela.academico.repository.GrupoRepository;
import escuela.admin.dto.CapturaAsistenciaForm;
import escuela.asistencia.dto.HojaAsistenciaResponse;
import escuela.asistencia.repository.AsistenciaRepository;
import escuela.asistencia.service.AsistenciaService;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsistenciaAdminCapturaControllerTest {
    @Mock private AsistenciaService service;
    @Mock private AsistenciaRepository repository;
    @Mock private GrupoRepository grupos;
    @Mock private InstitucionService instituciones;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private AsistenciaAdminController controller;

    @Test
    void abreListaConsultandoLaInstitucionSinNavegarRelacionesPerezosas() {
        LocalDate fecha = LocalDate.of(2026, 9, 29);
        var hoja = new HojaAsistenciaResponse(7L, "1 A", "Primero", "Centro",
                "2026-2027", fecha, List.of());
        when(service.hoja(7L, fecha)).thenReturn(hoja);
        when(grupos.findInstitucionIdById(7L)).thenReturn(Optional.of(3L));
        when(instituciones.listar()).thenReturn(List.of());
        when(alcance.filtrarInstituciones(List.of())).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.captura(7L, fecha, model);

        assertThat(vista).isEqualTo("admin/asistencia-captura");
        assertThat(model.get("hoja")).isSameAs(hoja);
        assertThat(((CapturaAsistenciaForm) model.get("form")).getInstitucionId()).isEqualTo(3L);
    }
}
