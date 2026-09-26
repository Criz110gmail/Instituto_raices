package escuela.admin.controller;

import escuela.admin.dto.TutorForm;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioService;
import escuela.tutor.dto.request.TutorRequest;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.entity.TipoIdentificacionTutor;
import escuela.tutor.service.IdentificacionTutorService;
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
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TutorAdminControllerTest {

    @Mock private TutorService service;
    @Mock private IdentificacionTutorService identificacionService;
    @Mock private InstitucionService institucionService;
    @Mock private UsuarioService usuarioService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private TutorAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(
                        alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void preparaFormularioYSeleccionaLaUnicaInstitucion() {
        when(institucionService.listar()).thenReturn(List.of(institucion()));
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.nuevo(model);

        assertThat(vista).isEqualTo("admin/tutor-form");
        assertThat(((TutorForm) model.get("form")).getInstitucionId()).isEqualTo(1L);
        assertThat(model).containsKeys("instituciones", "usuarioSeleccionado");
        assertThat(model.get("usuarioSeleccionado")).isEqualTo("");
    }

    @Test
    void creaTutorYRegresaAlListado() {
        TutorForm form = formulario();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

        String vista = controller.crear(form, errores, new ExtendedModelMap(), flash);

        ArgumentCaptor<TutorRequest> request = ArgumentCaptor.forClass(TutorRequest.class);
        verify(service).crear(request.capture());
        assertThat(request.getValue().telefonoPrincipal()).isEqualTo("3312345678");
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/tutores");
    }

    @Test
    void conservaFormularioCuandoLaCuentaYaEstaVinculada() {
        TutorForm form = formulario();
        when(service.crear(any())).thenThrow(
                new RecursoDuplicadoException("Ese usuario ya está vinculado con otro tutor"));
        when(institucionService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/tutor-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("otro tutor");
    }

    @Test
    void desactivaLogicamente() {
        when(service.obtener(10L)).thenReturn(tutor());

        String vista = controller.desactivar(10L, 4L, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        verify(service).desactivar(10L, 4L);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/tutores");
    }

    @Test
    void cargaIdentificacionOpcionalYRegresaAlExpediente() {
        when(service.obtener(10L)).thenReturn(tutor());
        MockMultipartFile archivo = new MockMultipartFile("archivo", "ine.pdf",
                "application/pdf", "%PDF-1.4".getBytes());

        String vista = controller.asignarIdentificacion(10L, TipoIdentificacionTutor.INE,
                archivo, new ExtendedModelMap(), new RedirectAttributesModelMap());

        verify(identificacionService).asignar(10L, TipoIdentificacionTutor.INE, archivo);
        assertThat(vista).isEqualTo("redirect:/admin/tutores/10/editar");
    }

    private TutorForm formulario() {
        TutorForm form = new TutorForm();
        form.setInstitucionId(1L);
        form.setNombres("María");
        form.setPrimerApellido("López");
        form.setTelefonoPrincipal("3312345678");
        return form;
    }

    private InstitucionResponse institucion() {
        return new InstitucionResponse(1L, "RAICES", "Instituto Raíces", null, null,
                null, null, null, null, null, null, null, null, "MX", null,
                "America/Mexico_City", "MXN", true, null);
    }

    private TutorResponse tutor() {
        Instant ahora = Instant.parse("2026-09-12T12:00:00Z");
        return new TutorResponse(10L, 1L, null, null, "María", "López", null,
                "3312345678", null, null, LocalDate.of(1990, 5, 10), null, null,
                null, null, null, null, null, "MX", null, null, null, true,
                new AuditoriaResponse(ahora, 1L, ahora, 1L, 4L));
    }
}
