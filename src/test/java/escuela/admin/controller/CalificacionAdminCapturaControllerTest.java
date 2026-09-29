package escuela.admin.controller;

import escuela.academico.entity.TipoEvaluacion;
import escuela.academico.repository.GrupoRepository;
import escuela.admin.dto.CapturaCalificacionesForm;
import escuela.calificacion.dto.HojaCalificacionesResponse;
import escuela.calificacion.repository.CalificacionRepository;
import escuela.calificacion.service.CalificacionService;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalificacionAdminCapturaControllerTest {
    @Mock private CalificacionService service;
    @Mock private CalificacionRepository repository;
    @Mock private AsignacionGrupoRepository asignacionRepository;
    @Mock private GrupoRepository grupoRepository;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private CalificacionAdminController controller;

    @Test
    void abreCapturaConsultandoLaInstitucionSinNavegarRelacionesPerezosas() {
        var hoja = new HojaCalificacionesResponse(7L, "1 A", "Centro", "2026-2027",
                "Primero", 8L, "Primer periodo", 9L, "Matemáticas",
                TipoEvaluacion.NUMERICA, BigDecimal.ZERO, BigDecimal.TEN,
                BigDecimal.valueOf(6), 1, false, List.of());
        when(service.hoja(7L, 8L, 9L)).thenReturn(hoja);
        when(grupoRepository.findInstitucionIdById(7L)).thenReturn(Optional.of(3L));
        when(institucionService.listar()).thenReturn(List.of());
        when(alcance.filtrarInstituciones(List.of())).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.captura(7L, 8L, 9L, model);

        assertThat(vista).isEqualTo("admin/calificacion-captura");
        assertThat(model.get("hoja")).isSameAs(hoja);
        assertThat(((CapturaCalificacionesForm) model.get("form")).getInstitucionId()).isEqualTo(3L);
    }
}
