package escuela.docente.controller;

import escuela.admin.support.MensajeErrorFormulario;
import escuela.docente.dto.*;
import escuela.docente.entity.EstadoPlaneacion;
import escuela.docente.service.*;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.*;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/maestros")
public class PortalMaestroController {
    private final PlaneacionService service;private final PdfPlaneacionService pdf;
    private final PortalAlumnoMaestroService alumnos;private final FotografiaMaestroService fotografias;

    @GetMapping String inicio(@RequestParam(defaultValue="0")int pagina,@AuthenticationPrincipal UsuarioPrincipal principal,Model m){var perfil=service.perfil(principal);m.addAttribute("perfil",perfil);m.addAttribute("fotoPerfil",fotografias.actual(perfil.id())!=null);m.addAttribute("planeaciones",service.listarMaestro(principal,pagina));return"maestros/inicio";}
    @GetMapping("/fotografia") ResponseEntity<Resource> fotografiaPropia(@AuthenticationPrincipal UsuarioPrincipal p){var perfil=service.perfil(p);var a=fotografias.descargar(perfil.id());return archivo(a);}
    @GetMapping("/alumnos") String alumnos(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")int pagina,@AuthenticationPrincipal UsuarioPrincipal p,Model m){var perfil=service.perfil(p);m.addAttribute("perfil",perfil);m.addAttribute("fotoPerfil",fotografias.actual(perfil.id())!=null);m.addAttribute("q",q);m.addAttribute("alumnos",alumnos.listar(p,q,pagina));return"maestros/alumnos";}
    @GetMapping("/alumnos/{id}") String alumno(@PathVariable Long id,@AuthenticationPrincipal UsuarioPrincipal p,Model m){var perfil=service.perfil(p);m.addAttribute("perfil",perfil);m.addAttribute("fotoPerfil",fotografias.actual(perfil.id())!=null);m.addAttribute("alumno",alumnos.detalle(p,id));return"maestros/alumno-detalle";}
    @GetMapping("/alumnos/{id}/fotografia") ResponseEntity<Resource> fotografiaAlumno(@PathVariable Long id,@AuthenticationPrincipal UsuarioPrincipal p){return archivo(alumnos.fotografia(p,id));}
    @GetMapping("/planeaciones/nueva") String nueva(@RequestParam(required=false)LocalDate inicio,@AuthenticationPrincipal UsuarioPrincipal principal,Model m){LocalDate i=inicio==null?LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)):inicio;PlaneacionForm f=new PlaneacionForm();f.setFechaInicio(i);f.setFechaFin(i.plusDays(4));f.getAlineaciones().add(new PlaneacionForm.AlineacionForm());var a=new PlaneacionForm.ActividadForm();a.setFecha(i);f.getActividades().add(a);preparar(m,f,null,principal);return"maestros/planeacion-form";}
    @PostMapping("/planeaciones") String crear(@Valid @ModelAttribute("form")PlaneacionForm f,BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal principal,Model m,RedirectAttributes flash){if(errores.hasErrors()){preparar(m,f,null,principal);return"maestros/planeacion-form";}try{Long id=service.crear(principal,f.request());flash.addFlashAttribute("mensaje","Borrador guardado. Puedes seguir editándolo antes de enviarlo");return"redirect:/maestros/planeaciones/"+id;}catch(RuntimeException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));preparar(m,f,null,principal);return"maestros/planeacion-form";}}
    @GetMapping("/planeaciones/{id}") String detalle(@PathVariable Long id,@AuthenticationPrincipal UsuarioPrincipal principal,Model m){m.addAttribute("perfil",service.perfil(principal));m.addAttribute("planeacion",service.detalleMaestro(principal,id));m.addAttribute("versionEntidad",service.versionEntidad(id));return"maestros/planeacion-detalle";}
    @GetMapping("/planeaciones/{id}/editar") String editar(@PathVariable Long id,@AuthenticationPrincipal UsuarioPrincipal principal,Model m){PlaneacionDocumento d=service.detalleMaestro(principal,id);preparar(m,PlaneacionForm.desde(d,service.versionEntidad(id)),id,principal);return"maestros/planeacion-form";}
    @PostMapping("/planeaciones/{id}") String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form")PlaneacionForm f,BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal principal,Model m,RedirectAttributes flash){if(errores.hasErrors()){preparar(m,f,id,principal);return"maestros/planeacion-form";}try{service.actualizar(principal,id,f.request());flash.addFlashAttribute("mensaje","Cambios guardados en el borrador");return"redirect:/maestros/planeaciones/"+id;}catch(RuntimeException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));preparar(m,f,id,principal);return"maestros/planeacion-form";}}
    @PostMapping("/planeaciones/{id}/enviar") String enviar(@PathVariable Long id,@RequestParam Long version,@AuthenticationPrincipal UsuarioPrincipal p,RedirectAttributes f){try{service.enviar(p,id,version);f.addFlashAttribute("mensaje","Planeación enviada a revisión");}catch(RuntimeException e){f.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}return"redirect:/maestros/planeaciones/"+id;}
    @PostMapping("/planeaciones/{id}/descartar") String descartar(@PathVariable Long id,@RequestParam Long version,@RequestParam String motivo,@AuthenticationPrincipal UsuarioPrincipal p,RedirectAttributes f){try{service.descartar(p,id,version,motivo);f.addFlashAttribute("mensaje","Planeación descartada; el historial se conserva");}catch(RuntimeException e){f.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}return"redirect:/maestros/planeaciones/"+id;}
    @GetMapping("/api/materias") @ResponseBody List<PlaneacionDocumento.Materia> materias(@RequestParam Long grupoId,@RequestParam LocalDate fechaInicio,@RequestParam LocalDate fechaFin,@AuthenticationPrincipal UsuarioPrincipal p){return service.materiasDisponibles(p,grupoId,fechaInicio,fechaFin);}
    @GetMapping("/planeaciones/{id}/pdf") void pdf(@PathVariable Long id,@AuthenticationPrincipal UsuarioPrincipal p,HttpServletResponse r)throws IOException{r.setContentType("application/pdf");r.setHeader("Content-Disposition",disposicion("planeacion-"+id+".pdf"));pdf.exportar(service.detalleMaestro(p,id),r.getOutputStream());}
    private void preparar(Model m,PlaneacionForm f,Long id,UsuarioPrincipal p){m.addAttribute("form",f);m.addAttribute("id",id);m.addAttribute("edicion",id!=null);m.addAttribute("perfil",service.perfil(p));m.addAttribute("grupos",service.gruposDisponibles(p,f.getFechaInicio(),f.getFechaFin()));m.addAttribute("materias",f.getGrupoId()==null?List.of():service.materiasDisponibles(p,f.getGrupoId(),f.getFechaInicio(),f.getFechaFin()));}
    private String disposicion(String nombre){return"inline; filename*=UTF-8''"+URLEncoder.encode(nombre,StandardCharsets.UTF_8);}
    private ResponseEntity<Resource> archivo(escuela.archivo.dto.ArchivoDescarga a){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType(a.tipoMime())).contentLength(a.tamanoBytes()).header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.inline().filename(a.nombreOriginal(),StandardCharsets.UTF_8).build().toString()).body(a.recurso());}
}
