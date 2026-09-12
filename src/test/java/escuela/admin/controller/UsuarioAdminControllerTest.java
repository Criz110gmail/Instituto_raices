package escuela.admin.controller;

import escuela.admin.dto.AsignacionRolForm;
import escuela.admin.dto.UsuarioForm;
import escuela.common.dto.response.AuditoriaResponse;
import escuela.common.exception.RecursoDuplicadoException;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.dto.request.AsignacionRolRequest;
import escuela.seguridad.dto.response.RecuperacionPasswordEmitidaResponse;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.AlcanceRol;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.AdministracionAccesoService;
import escuela.seguridad.service.InvitacionUsuarioService;
import escuela.seguridad.service.RecuperacionPasswordService;
import escuela.seguridad.service.RolService;
import escuela.seguridad.service.UsuarioService;
import escuela.seguridad.service.AlcanceDatosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.time.Instant;
import java.time.Duration;
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
    @Mock private RecuperacionPasswordService recuperacionPasswordService;
    @Mock private InstitucionService institucionService;
    @Mock private PlantelService plantelService;
    @Mock private AlcanceDatosService alcance;
    @InjectMocks private UsuarioAdminController controller;

    @BeforeEach
    void prepararAlcance() {
        org.mockito.Mockito.lenient().when(alcance.filtrarInstituciones(org.mockito.ArgumentMatchers.anyList()))
                .thenAnswer(i -> i.getArgument(0));
    }

    @AfterEach
    void limpiarSolicitud() {
        RequestContextHolder.resetRequestAttributes();
    }

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

    @Test
    void generaEnlaceDeRecuperacionPorTreintaMinutos() {
        when(service.obtener(7L)).thenReturn(usuario());
        when(recuperacionPasswordService.emitir(7L, Duration.ofMinutes(30)))
                .thenReturn(new RecuperacionPasswordEmitidaResponse(
                        "token-seguro", Instant.parse("2026-09-11T12:30:00Z")));
        MockHttpServletRequest solicitud = new MockHttpServletRequest();
        solicitud.setScheme("https");
        solicitud.setServerName("nexo.example");
        solicitud.setServerPort(443);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(solicitud));
        RedirectAttributesModelMap flash = new RedirectAttributesModelMap();

        String vista = controller.emitirRecuperacionPassword(
                7L, new ExtendedModelMap(), flash);

        verify(recuperacionPasswordService).emitir(7L, Duration.ofMinutes(30));
        assertThat(flash.getFlashAttributes().get("recuperacionEnlace"))
                .isEqualTo("https://nexo.example/restablecer-password?token=token-seguro");
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
