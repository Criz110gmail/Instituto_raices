package escuela.admin.controller;

import escuela.admin.dto.AlumnoTutorForm;
import escuela.alumno.dto.request.AlumnoTutorRequest;
import escuela.alumno.dto.response.AlumnoTutorResponse;
import escuela.alumno.entity.ParentescoTutor;
import escuela.alumno.service.AlumnoService;
import escuela.alumno.service.AlumnoTutorService;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.tutor.service.TutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoTutorAdminControllerTest {

    @Mock private AlumnoTutorService service;
    @Mock private AlumnoService alumnoService;
    @Mock private TutorService tutorService;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private AlumnoTutorAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(
                        alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void preparaFormularioConInstitucionYCatalogos() {
        when(institucionService.listar()).thenReturn(List.of(institucion()));
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.nuevo(model);

        assertThat(vista).isEqualTo("admin/alumno-tutor-form");
        assertThat(((AlumnoTutorForm) model.get("form")).getInstitucionId()).isEqualTo(1L);
        assertThat(model).containsKeys("instituciones", "alumnos", "tutores", "parentescos");
        verify(alumnoService).listarActivosPorInstitucion(1L);
        verify(tutorService).listarActivosPorInstitucion(1L);
    }

    @Test
    void creaVinculoYRegresaAlListado() {
        AlumnoTutorForm form = formulario();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        ArgumentCaptor<AlumnoTutorRequest> request = ArgumentCaptor.forClass(AlumnoTutorRequest.class);
        verify(service).crear(request.capture());
        assertThat(request.getValue().parentesco()).isEqualTo(ParentescoTutor.MADRE);
        assertThat(request.getValue().puedeRecoger()).isTrue();
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/vinculos-tutor");
    }

    @Test
    void conservaFormularioCuandoSeDuplicaContactoPrincipal() {
        AlumnoTutorForm form = formulario();
        when(service.crear(any())).thenThrow(
                new RecursoDuplicadoException("El alumno ya tiene un contacto principal"));
        when(institucionService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model,
                new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/alumno-tutor-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("contacto principal");
    }

    @Test
    void revocaLogicamente() {
        when(service.obtener(30L)).thenReturn(vinculo());

        String vista = controller.desactivar(30L, 4L, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        verify(service).desactivar(30L, 4L);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/vinculos-tutor");
    }

    private AlumnoTutorForm formulario() {
        AlumnoTutorForm form = new AlumnoTutorForm();
        form.setInstitucionId(1L);
        form.setAlumnoId(10L);
        form.setTutorId(20L);
        form.setParentesco(ParentescoTutor.MADRE);
        form.setPuedeRecoger(true);
        return form;
    }

    private InstitucionResponse institucion() {
        return new InstitucionResponse(1L, "RAICES", "Instituto Raíces", null, null,
                null, null, null, null, null, null, null, null, "MX", null,
                "America/Mexico_City", "MXN", true, null);
    }

    private AlumnoTutorResponse vinculo() {
        Instant ahora = Instant.parse("2026-09-12T12:00:00Z");
        return new AlumnoTutorResponse(30L, 1L, 10L, "A-001", "Ana López",
                20L, "María López", ParentescoTutor.MADRE, null, true, true,
                true, true, true, true, LocalDate.of(2026, 1, 1), null,
                null, true, new AuditoriaResponse(ahora, 1L, ahora, 1L, 4L));
    }
}
