package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.dto.*;
import escuela.alumno.entity.*;
import escuela.alumno.service.*;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/actualizaciones-expediente")
public class ActualizacionExpedienteAdminController {
    private final ActualizacionExpedienteFamiliarService service;
    private final ExcelActualizacionExpedienteService excel;
    private final InstitucionService instituciones;
    private final AlcanceDatosService alcance;

    @GetMapping String listar(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)TipoActualizacionExpediente tipo,
        @RequestParam(required=false)EstadoActualizacionExpediente estado,@RequestParam(required=false)LocalDate desde,
        @RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String texto,
        @RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,
        @AuthenticationPrincipal UsuarioPrincipal principal,Authentication auth,Model model){
        if(institucionId==null)institucionId=principal.institucionId();FiltroActualizacionExpediente f=new FiltroActualizacionExpediente(institucionId,null,tipo,estado,desde,hasta,texto,pagina,tamanio).normalizado();
        try{model.addAttribute("resultado",service.listarAdministracion(f));}catch(RuntimeException ex){model.addAttribute("errorFiltro",MensajeErrorFormulario.desde(ex));model.addAttribute("resultado",org.springframework.data.domain.Page.empty());}
        model.addAttribute("filtro",f);model.addAttribute("instituciones",alcance.filtrarInstituciones(instituciones.listar()));model.addAttribute("tipos",TipoActualizacionExpediente.values());model.addAttribute("estados",EstadoActualizacionExpediente.values());menu(model,auth);return"admin/actualizaciones-expediente";
    }

    @GetMapping("/excel") void excel(@RequestParam Long institucionId,@RequestParam(required=false)TipoActualizacionExpediente tipo,@RequestParam(required=false)EstadoActualizacionExpediente estado,@RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String texto,HttpServletResponse response)throws IOException{response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=actualizaciones-expediente.xlsx");excel.exportar(new FiltroActualizacionExpediente(institucionId,null,tipo,estado,desde,hasta,texto,0,100),response.getOutputStream());}

    @GetMapping("/{id}") String detalle(@PathVariable Long id,Authentication auth,Model model){prepararDetalle(model,id,auth);return"admin/actualizacion-expediente-detalle";}
    @PostMapping("/{id}/aprobar") String aprobar(@PathVariable Long id,@Valid @ModelAttribute("revision")RevisionActualizacionForm form,BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal principal,Authentication auth,Model model,RedirectAttributes flash){if(errores.hasErrors()){prepararDetalle(model,id,auth);return"admin/actualizacion-expediente-detalle";}try{service.aprobar(id,form.getVersion(),form.getRespuesta(),principal);flash.addFlashAttribute("mensaje","Propuesta aprobada e incorporada al expediente oficial");return"redirect:/admin/actualizaciones-expediente/"+id;}catch(RuntimeException ex){model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararDetalle(model,id,auth);return"admin/actualizacion-expediente-detalle";}}
    @PostMapping("/{id}/rechazar") String rechazar(@PathVariable Long id,@Valid @ModelAttribute("revision")RevisionActualizacionForm form,BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal principal,Authentication auth,Model model,RedirectAttributes flash){if(errores.hasErrors()){prepararDetalle(model,id,auth);return"admin/actualizacion-expediente-detalle";}try{service.rechazar(id,form.getVersion(),form.getRespuesta(),principal);flash.addFlashAttribute("mensaje","Propuesta rechazada; la familia podrá consultar la respuesta");return"redirect:/admin/actualizaciones-expediente/"+id;}catch(RuntimeException ex){model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararDetalle(model,id,auth);return"admin/actualizacion-expediente-detalle";}}
    @GetMapping("/{id}/archivo") ResponseEntity<Resource> archivo(@PathVariable Long id){ArchivoDescarga a=service.archivoAdministracion(id);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType(a.tipoMime())).contentLength(a.tamanoBytes()).header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.inline().filename(a.nombreOriginal(),StandardCharsets.UTF_8).build().toString()).body(a.recurso());}
    private void prepararDetalle(Model model,Long id,Authentication auth){var d=service.detalleAdministracion(id);model.addAttribute("detalle",d);if(!model.containsAttribute("revision")){RevisionActualizacionForm f=new RevisionActualizacionForm();f.setVersion(d.version());model.addAttribute("revision",f);}menu(model,auth);}
    private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.ACTUALIZACIONES_EXPEDIENTE);m.addAttribute("puedeRevisar",p.contains("ACTUALIZACION_EXPEDIENTE_REVISAR"));}
}
