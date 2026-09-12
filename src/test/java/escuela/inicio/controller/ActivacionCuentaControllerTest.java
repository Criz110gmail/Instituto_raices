package escuela.inicio.controller;

import escuela.admin.dto.ActivacionCuentaForm;
import escuela.seguridad.service.InvitacionUsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivacionCuentaControllerTest {

    @Mock private InvitacionUsuarioService service;
    @InjectMocks private ActivacionCuentaController controller;

    @Test
    void rechazaPasswordsQueNoCoincidenEnLaMismaPantalla() {
        ActivacionCuentaForm form = formulario("otra-password-segura");
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.activar(form, errores, new ExtendedModelMap());

        assertThat(vista).isEqualTo("activar-cuenta");
        assertThat(errores.getFieldError("confirmarPassword")).isNotNull();
        verify(service, never()).activar(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void consumeInvitacionCuandoLasPasswordsCoinciden() {
        ActivacionCuentaForm form = formulario("password-muy-segura");
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.activar(form, errores, model);

        verify(service).activar(form.request());
        assertThat(vista).isEqualTo("activar-cuenta");
        assertThat(model.get("activada")).isEqualTo(true);
    }

    private ActivacionCuentaForm formulario(String confirmacion) {
        ActivacionCuentaForm form = new ActivacionCuentaForm();
        form.setToken("token-valido");
        form.setPassword("password-muy-segura");
        form.setConfirmarPassword(confirmacion);
        return form;
    }
}
