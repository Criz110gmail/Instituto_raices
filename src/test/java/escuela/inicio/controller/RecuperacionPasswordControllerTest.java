package escuela.inicio.controller;

import escuela.admin.dto.RestablecerPasswordForm;
import escuela.seguridad.service.RecuperacionPasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecuperacionPasswordControllerTest {

    @Mock private RecuperacionPasswordService service;
    @InjectMocks private RecuperacionPasswordController controller;

    @Test
    void rechazaPasswordsDistintasEnLaMismaPantalla() {
        RestablecerPasswordForm form = formulario("otra-password-segura");
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");

        String vista = controller.restablecer(form, errores, new ExtendedModelMap());

        assertThat(vista).isEqualTo("restablecer-password");
        assertThat(errores.getFieldError("confirmarPassword")).isNotNull();
        verify(service, never()).restablecer(any());
    }

    @Test
    void consumeTokenCuandoLasPasswordsCoinciden() {
        RestablecerPasswordForm form = formulario("password-muy-segura");
        BeanPropertyBindingResult errores = new BeanPropertyBindingResult(form, "form");
        ExtendedModelMap model = new ExtendedModelMap();

        String vista = controller.restablecer(form, errores, model);

        verify(service).restablecer(form.request());
        assertThat(vista).isEqualTo("restablecer-password");
        assertThat(model.get("restablecida")).isEqualTo(true);
    }

    private RestablecerPasswordForm formulario(String confirmacion) {
        RestablecerPasswordForm form = new RestablecerPasswordForm();
        form.setToken("token-valido");
        form.setPassword("password-muy-segura");
        form.setConfirmarPassword(confirmacion);
        return form;
    }
}
