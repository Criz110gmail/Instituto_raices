package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.RolForm;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.dto.request.RolRequest;
import escuela.seguridad.dto.response.RolResponse;
import escuela.seguridad.service.RolService;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolAdminControllerTest {

    @Mock private RolService service;
    @Mock private InstitucionService institucionService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private RolAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void creaRolConPermisosSeleccionados() {
        RolForm form = formulario();
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        ArgumentCaptor<RolRequest> request = ArgumentCaptor.forClass(RolRequest.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Set<Long>> permisos = ArgumentCaptor.forClass(Set.class);
        verify(service).crear(request.capture(), permisos.capture());
        assertThat(request.getValue().institucionId()).isEqualTo(1L);
        assertThat(permisos.getValue()).containsExactlyInAnyOrder(10L, 11L);
        assertThat(vista).isEqualTo("redirect:/admin/catalogos/roles");
    }

    @Test
    void muestraDuplicadoEnElMismoFormulario() {
        RolForm form = formulario();
        when(service.crear(any(), anySet())).thenThrow(
                new RecursoDuplicadoException("Ya existe un rol con ese código en la institución"));
        when(institucionService.listar()).thenReturn(List.of());
        when(service.listarPermisos()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/rol-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("ese código");
    }

    @Test
    void reconstruyePermisosYVersionAlEditar() {
        when(service.obtener(4L)).thenReturn(new RolResponse(
                4L, 1L, "CAJERO", "Cajero", "Registra cobros", true, auditoria()));
        when(service.permisosAsignados(4L)).thenReturn(Set.of(10L, 12L));
        when(institucionService.listar()).thenReturn(List.of());
        when(service.listarPermisos()).thenReturn(List.of());
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.editar(4L, model);

        RolForm form = (RolForm) model.get("form");
        assertThat(vista).isEqualTo("admin/rol-form");
        assertThat(form.getInstitucionId()).isEqualTo(1L);
        assertThat(form.getPermisoIds()).containsExactlyInAnyOrder(10L, 12L);
        assertThat(form.getVersion()).isEqualTo(3L);
        assertThat(ModuloCatalogo.ROLES.mantenimientoDisponible()).isTrue();
    }

    private RolForm formulario() {
        RolForm form = new RolForm();
        form.setInstitucionId(1L);
        form.setCodigo("ADMIN_PLANTEL");
        form.setNombre("Administrador de plantel");
        form.setDescripcion("Administra un plantel");
        form.setPermisoIds(Set.of(10L, 11L));
        form.setActivo(true);
        return form;
    }

    private AuditoriaResponse auditoria() {
        Instant ahora = Instant.parse("2026-09-11T12:00:00Z");
        return new AuditoriaResponse(ahora, null, ahora, null, 3L);
    }
}
