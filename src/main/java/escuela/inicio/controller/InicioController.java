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
    String accesoDenegado() {
        return "error/acceso-denegado";
    }

    @GetMapping("/salud")
    String salud() {
        return "redirect:/actuator/health";
    }
}
