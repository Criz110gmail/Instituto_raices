package escuela.inicio.controller;

import escuela.admin.dto.RestablecerPasswordForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.service.RecuperacionPasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class RecuperacionPasswordController {

    private final RecuperacionPasswordService service;

    @GetMapping("/restablecer-password")
    String formulario(@RequestParam(defaultValue = "") String token, Model model) {
        RestablecerPasswordForm form = new RestablecerPasswordForm();
        form.setToken(token);
        model.addAttribute("form", form);
        return "restablecer-password";
    }

    @PostMapping("/restablecer-password")
    String restablecer(@Valid @ModelAttribute("form") RestablecerPasswordForm form,
                       BindingResult errores, Model model) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmarPassword())) {
            errores.rejectValue("confirmarPassword", "recuperacion.password.coincidencia",
                    "Las contraseñas no coinciden");
        }
        if (errores.hasErrors()) {
            return "restablecer-password";
        }
        try {
            service.restablecer(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "restablecer-password";
        }
        model.addAttribute("restablecida", true);
        return "restablecer-password";
    }
}
