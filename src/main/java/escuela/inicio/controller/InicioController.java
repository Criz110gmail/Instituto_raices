package escuela.inicio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {
    @org.springframework.beans.factory.annotation.Autowired(required=false)
    private escuela.sitioweb.SitioService sitioService;
    @org.springframework.beans.factory.annotation.Value("${app.sitio.codigo:}")
    private String sitioCodigo = "";


    @GetMapping("/")
    String inicio(org.springframework.ui.Model model) {
        if(!sitioCodigo.isBlank() && sitioService!=null){
            var contenido=sitioService.publico(sitioCodigo);
            escuela.sitioweb.SitioController.prepararPublico(model,contenido,false,
                "/sitio/"+org.springframework.web.util.UriUtils.encodePathSegment(sitioCodigo,java.nio.charset.StandardCharsets.UTF_8)+"/imagenes/");
            return escuela.sitioweb.SitioController.vistaPublica(contenido);
        }
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
