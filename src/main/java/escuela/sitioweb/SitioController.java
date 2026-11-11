package escuela.sitioweb;

import escuela.seguridad.service.UsuarioPrincipal;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import java.util.*;

@Controller @RequiredArgsConstructor
public class SitioController {
    private final SitioService service;

    @org.springframework.web.bind.annotation.InitBinder("form")
    void campos(org.springframework.web.bind.WebDataBinder binder){binder.setAllowedFields("nombre","eslogan","portada","nosotros","oferta","talleres","inscripciones","principal","acento","logoId","portadaId","portadaAlt","version");}
    @GetMapping("/admin/diseno-web/resumen")
    String resumen(@AuthenticationPrincipal UsuarioPrincipal p, Model m) {
        m.addAttribute("form", service.borrador(p)); preparar(p,m);
        return "admin/sitio-resumen";
    }
    @GetMapping("/admin/diseno-web")
    String visual(@AuthenticationPrincipal UsuarioPrincipal p){service.autorizar(p,"SITIO_WEB_EDITAR");return "admin/sitio-visual";}
    @GetMapping("/admin/diseno-web/editar")
    String editar(@AuthenticationPrincipal UsuarioPrincipal p, Model m) {
        m.addAttribute("form",service.borrador(p)); return editor(p,m);
    }
    private void preparar(UsuarioPrincipal p,Model m) {
        m.addAttribute("historial",service.historial(p));
        m.addAttribute("revisiones",service.revisiones(p));
        m.addAttribute("codigo",service.codigo(p));
        m.addAttribute("imagenes",service.imagenes(p));
        m.addAttribute("puedePublicar",p.authorities().stream().anyMatch(a->a.getAuthority().equals("SITIO_WEB_PUBLICAR")));
    }
    private String editor(UsuarioPrincipal p,Model m){preparar(p,m);return "admin/sitio-editor";}

    @PostMapping("/admin/diseno-web")
    String guardar(@AuthenticationPrincipal UsuarioPrincipal p,@Valid @ModelAttribute("form") SitioForm f,
                   BindingResult errores,Model m) {
        service.autorizar(p,"SITIO_WEB_EDITAR");
        if(!errores.hasErrors())try{var actual=service.borrador(p);f.setExtras(actual.getExtras());f.setDiseno(actual.getDiseno());service.guardar(p,f);return "redirect:/admin/diseno-web/editar?guardado";}
        catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());}
        return editor(p,m);
    }
    @PostMapping("/admin/diseno-web/publicar")
    String publicar(@AuthenticationPrincipal UsuarioPrincipal p,@RequestParam long version,Model m) {
        try{service.publicar(p,version);return "redirect:/admin/diseno-web?publicado";}
        catch(ReglaNegocioException e){m.addAttribute("form",service.borrador(p));m.addAttribute("errorOperacion",e.getMessage());preparar(p,m);return "admin/sitio-resumen";}
    }
    @GetMapping("/admin/diseno-web/vista-previa")
    String previa(@AuthenticationPrincipal UsuarioPrincipal p,@RequestParam(required=false) Long revision,Model m) {
        var sitio=revision==null?service.borrador(p):service.revision(p,revision);
        prepararPublico(m,sitio,true,"/admin/diseno-web/imagenes/");return vistaPublica(sitio);
    }
    @GetMapping("/admin/diseno-web/imagenes/nueva")
    String nuevaImagen(@AuthenticationPrincipal UsuarioPrincipal p,@RequestParam(required=false)String volver,Model m){service.autorizar(p,"SITIO_WEB_EDITAR");m.addAttribute("volver",retornoImagen(volver));return "admin/sitio-imagen";}
    private String retornoImagen(String volver){return volver!=null && volver.matches("/admin/diseno-web/galeria/[a-f0-9-]{36}/fotos(?:\\?[^\\r\\n#]*)?(?:#resultados)?")?volver:"/admin/diseno-web/editar#imagenes";}
    @PostMapping("/admin/diseno-web/imagenes")
    String subir(@AuthenticationPrincipal UsuarioPrincipal p,@RequestParam MultipartFile archivo,@RequestParam(required=false)String volver,Model m) {
        try{service.subir(p,archivo);return "redirect:"+retornoImagen(volver);}
        catch(ReglaNegocioException e){m.addAttribute("form",service.borrador(p));m.addAttribute("volver",retornoImagen(volver));m.addAttribute("errorOperacion",e.getMessage());return "admin/sitio-imagen";}
    }
    @GetMapping("/admin/diseno-web/imagenes/{imagenId}") @ResponseBody
    ResponseEntity<byte[]> privada(@AuthenticationPrincipal UsuarioPrincipal p,@PathVariable long imagenId) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).cacheControl(CacheControl.noStore()).body(service.imagenPrivada(p,imagenId));
    }
    @GetMapping("/sitio/{codigo}")
    String publico(@PathVariable String codigo,Model m) {
        var sitio=service.publico(codigo);prepararPublico(m,sitio,false,"/sitio/"+org.springframework.web.util.UriUtils.encodePathSegment(codigo,java.nio.charset.StandardCharsets.UTF_8)+"/imagenes/");return vistaPublica(sitio);
    }
    @GetMapping("/sitio/{codigo}/imagenes/{imagenId}") @ResponseBody
    ResponseEntity<byte[]> imagen(@PathVariable String codigo,@PathVariable long imagenId) {
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).cacheControl(CacheControl.noCache()).header("X-Content-Type-Options","nosniff").body(service.imagenPublica(codigo,imagenId));
    }
    public static String vistaPublica(SitioForm sitio){return sitio.getDiseno()==null?"sitio-publicado":sitio.getDiseno().getBloques().stream().allMatch(b->"seccion".equals(b.getTipo()))?"sitio-disenador-publico":"sitio-visual-publico";}
    public static void prepararPublico(Model m,SitioForm sitio,boolean previa,String imagenBase) {
        m.addAttribute("sitio",sitio);m.addAttribute("previa",previa);
        if(sitio.getDiseno()!=null&&sitio.getDiseno().getBloques().stream().allMatch(b->"seccion".equals(b.getTipo())))m.addAttribute("disenadorHtml",new SitioDisenadorRender().documento(sitio,imagenBase));
        m.addAttribute("logoUrl",sitio.getLogoId()==null?null:imagenBase+sitio.getLogoId());
        m.addAttribute("portadaUrl",sitio.getPortadaId()==null?null:imagenBase+sitio.getPortadaId());
        m.addAttribute("imagenBase",imagenBase);
        m.addAttribute("albumesPublicos",sitio.getExtras().getAlbumes().stream().filter(a->a.isActivo()&&!a.getFotosPublicas().isEmpty()).sorted(java.util.Comparator.comparingInt(SitioExtras.Album::getOrden).thenComparing(SitioExtras.Album::getId)).toList());
        m.addAttribute("ubicacionesPublicas",sitio.getExtras().getUbicaciones().stream().filter(SitioExtras.Ubicacion::isActivo).sorted(java.util.Comparator.comparingInt(SitioExtras.Ubicacion::getOrden).thenComparing(SitioExtras.Ubicacion::getId)).toList());
        m.addAttribute("contacto",sitio.getExtras().getContacto());
        m.addAttribute("escolarCompleto",sitio.getDiseno()!=null&&sitio.getDiseno().getBloques().stream().anyMatch(b->"escolar".equals(b.getTipo())));m.addAttribute("bloques",sitio.getDiseno()==null?java.util.List.of():sitio.getDiseno().getBloques().stream().filter(SitioDiseno.Bloque::isVisible).toList());
    }
}
