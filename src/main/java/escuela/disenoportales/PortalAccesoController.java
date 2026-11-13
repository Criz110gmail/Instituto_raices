package escuela.disenoportales;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.http.*;
import jakarta.servlet.http.HttpServletResponse;
@Controller @RequiredArgsConstructor
public class PortalAccesoController {
 private final PortalIdentidadService service;
 @GetMapping("/accesos/{codigo}/{portal}") String acceso(@PathVariable String codigo,@PathVariable String portal,Model m,HttpServletResponse r){var tipo=PortalTipo.de(portal);var t=service.publico(codigo,tipo);r.setHeader("Cache-Control","no-store");PortalVista.preparar(m,t,false,"/accesos/"+org.springframework.web.util.UriUtils.encodePathSegment(codigo,java.nio.charset.StandardCharsets.UTF_8)+"/"+tipo.codigo+"/logo.png",true);return tipo.plantilla;}
 @GetMapping("/accesos/{codigo}/{portal}/logo.png") ResponseEntity<byte[]> logo(@PathVariable String codigo,@PathVariable String portal){var t=service.publico(codigo,PortalTipo.de(portal));if(!t.isTieneLogo())return ResponseEntity.notFound().build();return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.IMAGE_PNG).body(t.logo());}
}
