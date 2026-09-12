package escuela.admin.controller;

import escuela.admin.dto.AsignacionRolForm;
import escuela.admin.dto.UsuarioForm;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.dto.request.AsignacionRolRequest;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.AdministracionAccesoService;
import escuela.seguridad.service.InvitacionUsuarioService;
import escuela.seguridad.service.RolService;
import escuela.seguridad.service.UsuarioService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioAdminControllerTest {

    @Mock private UsuarioService service;
    @Mock private RolService rolService;
    @Mock private AdministracionAccesoService accesoService;
    @Mock private InvitacionUsuarioService invitacionService;
    @Mock private InstitucionService institucionService;
    @Mock private PlantelService plantelService;
    @InjectMocks private UsuarioAdminController controller;

    @Test
    void creaInvitadoYAbreSuAdministracion() {
        UsuarioForm form = formulario();
        when(service.crearInvitado(any())).thenReturn(usuario());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.crear(form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        verify(service).crearInvitado(any());
        assertThat(vista).isEqualTo("redirect:/admin/usuarios/7/editar");
    }

    @Test
    void conservaFormularioCuandoElCorreoEstaDuplicado() {
        UsuarioForm form = formulario();
        when(service.crearInvitado(any())).thenThrow(
                new RecursoDuplicadoException("Ya existe un usuario con ese correo en la institución"));
        when(institucionService.listar()).thenReturn(List.of());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.crear(form, errores, model, new RedirectAttributesModelMap());

        assertThat(vista).isEqualTo("admin/usuario-form");
        assertThat(model.get("form")).isSameAs(form);
        assertThat(model.get("errorOperacion")).asString().contains("ese correo");
    }

    @Test
    void asignaRolAlUsuarioIndicadoPorLaRuta() {
        AsignacionRolForm form = new AsignacionRolForm();
        form.setRolId(3L);
        form.setAlcance(AlcanceRol.PLANTEL);
        form.setPlantelId(5L);
        when(service.obtener(7L)).thenReturn(usuario());
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "asignacionForm");

        String vista = controller.asignarRol(7L, form, errores, new ExtendedModelMap(),
                new RedirectAttributesModelMap());

        ArgumentCaptor<AsignacionRolRequest> request = ArgumentCaptor.forClass(AsignacionRolRequest.class);
        verify(accesoService).asignarRol(request.capture());
        assertThat(request.getValue().usuarioId()).isEqualTo(7L);
        assertThat(request.getValue().plantelId()).isEqualTo(5L);
        assertThat(vista).isEqualTo("redirect:/admin/usuarios/7/editar");
    }

    private UsuarioForm formulario() {
        UsuarioForm form = new UsuarioForm();
        form.setInstitucionId(1L);
        form.setUsername("admin.raices");
        form.setEmail("admin@raices.mx");
        return form;
    }

    private UsuarioResponse usuario() {
        Instant ahora = Instant.parse("2026-09-11T12:00:00Z");
        return new UsuarioResponse(7L, 1L, "admin.raices", "admin@raices.mx",
                EstadoUsuario.INVITADO, false,
                new AuditoriaResponse(ahora, null, ahora, null, 1L));
    }
}
