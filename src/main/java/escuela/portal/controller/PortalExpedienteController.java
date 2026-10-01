package escuela.portal.controller;

import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.dto.*;
import escuela.alumno.entity.*;
import escuela.alumno.service.ActualizacionExpedienteFamiliarService;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.institucion.service.InstitucionService;
import escuela.portal.service.PortalTutorService;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
@RequestMapping("/portal/expediente")
public class PortalExpedienteController {
    private final ActualizacionExpedienteFamiliarService actualizaciones;
    private final PortalTutorService portal;
    private final InstitucionService instituciones;

    @GetMapping
    String inicio(@RequestParam Long alumnoId,@RequestParam(defaultValue="0")int pagina,
                  @AuthenticationPrincipal UsuarioPrincipal principal,Model model){
        prepararComun(model,principal,alumnoId);model.addAttribute("solicitudes",actualizaciones.listarPortal(principal,alumnoId,pagina));return"portal/expediente";
    }

    @GetMapping("/documento/nuevo")
    String documento(@RequestParam Long alumnoId,@AuthenticationPrincipal UsuarioPrincipal principal,Model model){
        prepararComun(model,principal,alumnoId);model.addAttribute("form",new PortalDocumentoPropuestaForm());model.addAttribute("tiposDocumento",TipoDocumentoAlumno.values());return"portal/expediente-documento-form";
    }

    @PostMapping("/documento")
    String guardarDocumento(@RequestParam Long alumnoId,@Valid @ModelAttribute("form")PortalDocumentoPropuestaForm form,
                            BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal principal,Model model,RedirectAttributes flash){
        if(errores.hasErrors()){prepararComun(model,principal,alumnoId);model.addAttribute("tiposDocumento",TipoDocumentoAlumno.values());return"portal/expediente-documento-form";}
        try{actualizaciones.proponerDocumento(principal,alumnoId,form);flash.addFlashAttribute("mensajePortal","Documento enviado para revisión administrativa");return"redirect:/portal/expediente?alumnoId="+alumnoId;}
        catch(RuntimeException ex){model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararComun(model,principal,alumnoId);model.addAttribute("tiposDocumento",TipoDocumentoAlumno.values());return"portal/expediente-documento-form";}
    }

    @GetMapping("/medica/nueva")
    String medica(@RequestParam Long alumnoId,@AuthenticationPrincipal UsuarioPrincipal principal,Model model){
        prepararComun(model,principal,alumnoId);model.addAttribute("form",actualizaciones.formularioMedico(principal,alumnoId));model.addAttribute("tiposSanguineos",TipoSanguineo.values());return"portal/expediente-medico-form";
    }

    @PostMapping("/medica")
    String guardarMedica(@RequestParam Long alumnoId,@Valid @ModelAttribute("form")PortalFichaMedicaPropuestaForm form,
                         BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal principal,Model model,RedirectAttributes flash){
        if(errores.hasErrors()){prepararComun(model,principal,alumnoId);model.addAttribute("tiposSanguineos",TipoSanguineo.values());return"portal/expediente-medico-form";}
        try{actualizaciones.proponerFichaMedica(principal,alumnoId,form);flash.addFlashAttribute("mensajePortal","Información médica enviada para revisión administrativa");return"redirect:/portal/expediente?alumnoId="+alumnoId;}
        catch(RuntimeException ex){model.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararComun(model,principal,alumnoId);model.addAttribute("tiposSanguineos",TipoSanguineo.values());return"portal/expediente-medico-form";}
    }

    @GetMapping("/solicitudes/{id}/archivo")
    ResponseEntity<Resource> archivo(@PathVariable Long id,@RequestParam Long alumnoId,@AuthenticationPrincipal UsuarioPrincipal principal){return respuesta(actualizaciones.archivoPortal(principal,alumnoId,id));}

    private void prepararComun(Model model,UsuarioPrincipal principal,Long alumnoId){model.addAttribute("hijo",portal.validarHijo(principal,alumnoId));model.addAttribute("institucion",instituciones.obtener(principal.institucionId()).nombre());model.addAttribute("consentimientoTexto",ActualizacionExpedienteFamiliarService.CONSENTIMIENTO_TEXTO);}
    private ResponseEntity<Resource> respuesta(ArchivoDescarga a){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType(a.tipoMime())).contentLength(a.tamanoBytes()).header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.inline().filename(a.nombreOriginal(),StandardCharsets.UTF_8).build().toString()).body(a.recurso());}
}
