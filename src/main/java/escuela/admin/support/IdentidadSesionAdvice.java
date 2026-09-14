package escuela.admin.support;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(basePackages = "escuela.admin")
public class IdentidadSesionAdvice {

    @ModelAttribute("usuarioSesion")
    String usuarioSesion(Authentication autenticacion) {
        return autenticacion == null ? "" : autenticacion.getName();
    }
}
