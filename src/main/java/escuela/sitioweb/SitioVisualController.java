package escuela.sitioweb;

import escuela.seguridad.service.UsuarioPrincipal;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/diseno-web/visual")
public class SitioVisualController {
    private final SitioService sitios;
    private final SitioCatalogosService catalogos;
    @org.springframework.beans.factory.annotation.Value("${app.sitio.codigo:}") private String sitioCodigo="";
    @GetMapping String editor(@AuthenticationPrincipal UsuarioPrincipal p){sitios.autorizar(p,"SITIO_WEB_EDITAR");return "admin/sitio-visual";}
    @GetMapping("/lienzo") String lienzo(@AuthenticationPrincipal UsuarioPrincipal p,Model m){sitios.autorizar(p,"SITIO_WEB_EDITAR");m.addAttribute("modoEditor",true);return "sitio-visual-lienzo";}
    @GetMapping("/datos") @ResponseBody Map<String,Object> datos(@AuthenticationPrincipal UsuarioPrincipal p){return Map.of("publicadaVersion",sitios.historial(p).stream().findFirst().map(r->((Number)r.get("version")).longValue()).orElse(0L),"publicacionUrl","/sitio/"+org.springframework.web.util.UriUtils.encodePathSegment(sitios.codigo(p),java.nio.charset.StandardCharsets.UTF_8),"inicioVinculado",sitioCodigo.equals(sitios.codigo(p)),"revisiones",sitios.revisiones(p),"usuario",p.usuarioId(),"sitio",sitios.borrador(p),"imagenes",sitios.imagenes(p),"codigo",sitios.codigo(p),"puedePublicar",p.authorities().stream().anyMatch(a->a.getAuthority().equals("SITIO_WEB_PUBLICAR")));}
    @GetMapping("/recursos") @ResponseBody Map<String,Object> recursos(@AuthenticationPrincipal UsuarioPrincipal p){sitios.autorizar(p,"SITIO_WEB_EDITAR");return Map.of("planteles",catalogos.planteles(p),"publicaciones",sitios.historial(p),"imagenes",sitios.imagenes(p));}
    @GetMapping("/publicada") @ResponseBody Map<String,Object> publicada(@AuthenticationPrincipal UsuarioPrincipal p){sitios.autorizar(p,"SITIO_WEB_EDITAR");if(sitioCodigo.isBlank())return Map.of("original",true);try{return Map.of("sitio",sitios.publico(sitios.codigo(p)));}catch(org.springframework.web.server.ResponseStatusException e){if(e.getStatusCode().value()!=404)throw e;return Map.of("original",true);}}
    @PostMapping("/datos") @ResponseBody Map<String,Object> guardar(@AuthenticationPrincipal UsuarioPrincipal p,@Valid @RequestBody SitioVisualSolicitud solicitud){var f=sitios.borrador(p);f.setVersion(solicitud.version());f.setNombre(solicitud.nombre());f.setEslogan(solicitud.eslogan());f.setPrincipal(solicitud.principal());f.setAcento(solicitud.acento());f.setLogoId(solicitud.logoId());f.setDiseno(solicitud.diseno());if(solicitud.extras()!=null)f.setExtras(solicitud.extras());sitios.guardar(p,f);return datos(p);}
    public record Restauracion(@jakarta.validation.constraints.Min(1) long revision,@jakarta.validation.constraints.Min(0) long version){}
    @PostMapping("/restaurar") @ResponseBody Map<String,Object> restaurar(@AuthenticationPrincipal UsuarioPrincipal p,@Valid @RequestBody Restauracion solicitud){sitios.restaurar(p,solicitud.revision(),solicitud.version());return datos(p);}
    public record Publicacion(@jakarta.validation.constraints.Min(1) long version){}
    @PostMapping("/publicar") @ResponseBody Map<String,Object> publicar(@AuthenticationPrincipal UsuarioPrincipal p,@Valid @RequestBody Publicacion solicitud){sitios.publicar(p,solicitud.version());return Map.of("mensaje","Versión publicada","version",solicitud.version());}
    @PostMapping("/imagenes") @ResponseBody Map<String,Object> imagen(@AuthenticationPrincipal UsuarioPrincipal p,@RequestParam MultipartFile archivo){long id=sitios.subir(p,archivo);return Map.of("id",id,"imagenes",sitios.imagenes(p));}
    @ExceptionHandler(ReglaNegocioException.class) @ResponseBody ResponseEntity<Map<String,String>> regla(ReglaNegocioException e){return ResponseEntity.status(409).body(Map.of("mensaje",e.getMessage()));}
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class) @ResponseBody ResponseEntity<Map<String,String>> campos(org.springframework.web.bind.MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("mensaje","Revisa los campos del diseño: "+e.getBindingResult().getFieldErrors().stream().findFirst().map(f->f.getField()+" "+f.getDefaultMessage()).orElse("captura incompleta")));}
}
