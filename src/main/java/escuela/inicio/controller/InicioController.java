package escuela.inicio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping("/")
    String inicio() {
        return "inicio";
    }

    @GetMapping("/acceso-denegado")
    String accesoDenegado(org.springframework.security.core.Authentication authentication, org.springframework.ui.Model model) {
        model.addAttribute("rutaRegreso", escuela.seguridad.service.DestinoPortalSesion.inicio(authentication));
        return "error/acceso-denegado";
    }

    @GetMapping("/salud")
    String salud() {
        return "redirect:/actuator/health";
    }
}
