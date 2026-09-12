package escuela.admin.controller;

import escuela.admin.dto.AlumnoForm;
import escuela.alumno.dto.request.AlumnoRequest;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.service.AlumnoService;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
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
class AlumnoAdminControllerTest {

    @Mock private AlumnoService service;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private AlumnoAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(
                        alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void preparaFormularioNuevo() {
        when(institucionService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.nuevo(model);

        assertThat(vista).isEqualTo("admin/alumno-form");
        assertThat(model.get("form")).isInstanceOf(AlumnoForm.class);
        assertThat(model.get("edicion")).isEqualTo(false);
    }

    @Test
    void creaAlumnoYRegresaAlListado() {
        AlumnoForm form = formulario();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

        String vista = controller.crear(form, errores, new ExtendedModelMap(), flash);

        ArgumentCaptor<AlumnoRequest> request = ArgumentCaptor.forClass(AlumnoRequest.class);
        verify(service).crear(request.capture());
        assertThat(request.getValue().matricula()).isEqualTo("ALU-001");
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/alumnos");
        assertThat(flash.getFlashAttributes().get("mensaje"))
                .isEqualTo("Alumno registrado correctamente");
    }

    @Test
    void conservaFormularioCuandoLaMatriculaEstaDuplicada() {
        AlumnoForm form = formulario();
        when(service.crear(any())).thenThrow(
                new RecursoDuplicadoException("Ya existe un alumno con esa matrícula en la institución"));
        when(institucionService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/alumno-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("matrícula");
    }

    @Test
    void desactivaLogicamenteElAlumno() {
        when(service.obtener(10L)).thenReturn(alumno());

        String vista = controller.desactivar(10L, 4L, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        verify(service).desactivar(10L, 4L);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/alumnos");
    }

    private AlumnoForm formulario() {
        AlumnoForm form = new AlumnoForm();
        form.setInstitucionId(1L);
        form.setMatricula("ALU-001");
        form.setNombres("Ana");
        form.setPrimerApellido("Pérez");
        form.setFechaNacimiento(LocalDate.of(2018, 1, 1));
        form.setFechaIngreso(LocalDate.of(2024, 8, 20));
        return form;
    }

    private AlumnoResponse alumno() {
        Instant ahora = Instant.parse("2026-09-12T12:00:00Z");
        return new AlumnoResponse(10L, 1L, "ALU-001", "Ana", "Pérez", null,
                null, LocalDate.of(2018, 1, 1), null, null, null, null, null,
                null, null, null, null, null, null, null, "MX",
                LocalDate.of(2024, 8, 20), null, true,
                new AuditoriaResponse(ahora, 1L, ahora, 1L, 4L));
    }
}
