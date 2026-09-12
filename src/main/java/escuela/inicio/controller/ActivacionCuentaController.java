package escuela.inicio.controller;

import escuela.admin.dto.ActivacionCuentaForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.service.InvitacionUsuarioService;
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
public class ActivacionCuentaController {

    private final InvitacionUsuarioService service;

    @GetMapping("/activar-cuenta")
    String formulario(@RequestParam(defaultValue = "") String token, Model model) {
        ActivacionCuentaForm form = new ActivacionCuentaForm();
        form.setToken(token);
        model.addAttribute("form", form);
        return "activar-cuenta";
    }

    @PostMapping("/activar-cuenta")
    String activar(@Valid @ModelAttribute("form") ActivacionCuentaForm form,
                   BindingResult errores, Model model) {
        if (form.getPassword() != null && !form.getPassword().equals(form.getConfirmarPassword())) {
            errores.rejectValue("confirmarPassword", "activacion.password.coincidencia",
                    "Las contraseñas no coinciden");
        }
        if (errores.hasErrors()) {
            return "activar-cuenta";
        }
        try {
            service.activar(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "activar-cuenta";
        }
        model.addAttribute("activada", true);
        return "activar-cuenta";
    }
}
