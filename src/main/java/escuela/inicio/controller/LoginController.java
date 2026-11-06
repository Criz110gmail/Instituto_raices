package escuela.inicio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @GetMapping("/login")
    String login(@RequestParam(required = false) String origen) {
        if ("personal".equals(origen)) return "login-personal";
        if ("maestros".equals(origen)) return "login-maestros";
        if ("familias".equals(origen)) return "login-familias";
        return "login";
    }

    @GetMapping("/familias")
    String familias() { return "login-familias"; }

    @GetMapping("/personal/acceso")
    String personal() { return "login-personal"; }

    @GetMapping("/maestros/acceso")
    String maestros() { return "login-maestros"; }
}
