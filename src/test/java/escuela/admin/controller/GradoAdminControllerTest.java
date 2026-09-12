package escuela.admin.controller;

import escuela.academico.dto.request.GradoRequest;
import escuela.academico.dto.response.GradoResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.service.GradoService;
import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.GradoForm;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.ConflictoVersionException;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GradoAdminControllerTest {

    @Mock private GradoService gradoService;
    @Mock private NivelEducativoService nivelService;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private GradoAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
        org.mockito.Mockito.lenient().when(alcance.filtrarNiveles(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void preparaElFormularioNuevoConSusCatalogos() {
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.nuevo(model);

        assertThat(vista).isEqualTo("admin/grado-form");
        assertThat(model.get("form")).isInstanceOf(GradoForm.class);
        assertThat(model.get("edicion")).isEqualTo(false);
        assertThat(model).containsKeys("instituciones", "niveles");
    }

    @Test
    void creaElGradoConElNivelValidado() {
        GradoForm form = formulario(1L, 10L);
        when(nivelService.obtener(10L)).thenReturn(nivel(10L, 1L));
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

        String vista = controller.crear(form, errores, new ExtendedModelMap(), flash);

        ArgumentCaptor<GradoRequest> request = ArgumentCaptor.forClass(GradoRequest.class);
        verify(gradoService).crear(request.capture());
        assertThat(request.getValue().nivelEducativoId()).isEqualTo(10L);
        assertThat(request.getValue().codigo()).isEqualTo("PRIM-1");
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/grados");
        assertThat(flash.getFlashAttributes().get("mensaje"))
                .isEqualTo("Grado creado correctamente");
    }

    @Test
    void rechazaUnNivelDeOtraInstitucion() {
        GradoForm form = formulario(1L, 10L);
        when(nivelService.obtener(10L)).thenReturn(nivel(10L, 2L));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/grado-form");
        assertThat(errores.getFieldError("nivelEducativoId")).isNotNull();
        verify(gradoService, never()).crear(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void muestraElErrorDeNegocioEnElMismoFormulario() {
        GradoForm form = formulario(1L, 10L);
        when(nivelService.obtener(10L)).thenReturn(nivel(10L, 1L));
        when(gradoService.crear(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new RecursoDuplicadoException("Ya existe un grado con ese código en el nivel"));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/grado-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion"))
                .isEqualTo("Ya existe un grado con ese código en el nivel");
    }

    @Test
    void reconstruyeLaInstitucionPropietariaAlEditar() {
        when(gradoService.obtener(7L)).thenReturn(new GradoResponse(
                7L, 10L, "PRIM-1", "Primer grado", 1, true, auditoria()));
        when(nivelService.obtener(10L)).thenReturn(nivel(10L, 1L));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.editar(7L, model);

        GradoForm form = (GradoForm) model.get("form");
        assertThat(vista).isEqualTo("admin/grado-form");
        assertThat(form.getInstitucionId()).isEqualTo(1L);
        assertThat(form.getNivelEducativoId()).isEqualTo(10L);
        assertThat(form.getVersion()).isEqualTo(3L);
        assertThat(model.get("edicion")).isEqualTo(true);
    }

    @Test
    void mantieneLaEdicionCuandoFallaLaDesactivacionPorConcurrencia() {
        doThrow(new ConflictoVersionException("Grado", 7L))
                .when(gradoService).desactivar(7L, 2L);
        when(gradoService.obtener(7L)).thenReturn(new GradoResponse(
                7L, 10L, "PRIM-1", "Primer grado", 1, true, auditoria()));
        when(nivelService.obtener(10L)).thenReturn(nivel(10L, 1L));
        when(institucionService.listar()).thenReturn(List.of());
        when(nivelService.listar()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.desactivar(
                7L, 2L, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/grado-form");
        assertThat(model.get("errorOperacion")).asString()
                .contains("modificado por otro proceso");
        assertThat(((GradoForm) model.get("form")).getVersion()).isEqualTo(3L);
    }

    private GradoForm formulario(Long institucionId, Long nivelId) {
        GradoForm form = new GradoForm();
        form.setInstitucionId(institucionId);
        form.setNivelEducativoId(nivelId);
        form.setCodigo("PRIM-1");
        form.setNombre("Primer grado");
        form.setOrden(1);
        form.setActivo(true);
        return form;
    }

    private NivelEducativoResponse nivel(Long id, Long institucionId) {
        return new NivelEducativoResponse(id, institucionId, "PRIM", "Primaria", null,
                1, true, auditoria());
    }

    private AuditoriaResponse auditoria() {
        Instant ahora = Instant.parse("2026-09-11T12:00:00Z");
        return new AuditoriaResponse(ahora, null, ahora, null, 3L);
    }
}
