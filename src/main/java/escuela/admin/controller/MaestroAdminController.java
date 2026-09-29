package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.docente.dto.*;
import escuela.docente.service.*;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.dto.response.*;
import escuela.seguridad.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import org.springframework.core.io.Resource;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/maestros")
public class MaestroAdminController {
    private final MaestroService service; private final AccesoPortalMaestroService acceso;
    private final InstitucionService instituciones; private final AlcanceDatosService alcance;
    private final InvitacionUsuarioService invitaciones; private final RecuperacionPasswordService recuperaciones;
    private final FotografiaMaestroService fotografias;

    @GetMapping("/nuevo") String nuevo(Model m){preparar(m,new MaestroForm(),null);return "admin/maestro-form";}
    @PostMapping String crear(@Valid @ModelAttribute("form") MaestroForm f,BindingResult e,Model m,RedirectAttributes flash){validarInstitucion(f.getInstitucionId());if(e.hasErrors()){preparar(m,f,null);return"admin/maestro-form";}try{MaestroResponse x=service.crear(f.request());flash.addFlashAttribute("mensaje","Maestro registrado; ahora puedes asignarle grupos y materias");return"redirect:/admin/maestros/"+x.id()+"/editar";}catch(RuntimeException ex){return error(m,f,null,ex);}}
    @GetMapping("/{id}/editar") String editar(@PathVariable Long id,Model m){MaestroResponse x=autorizado(id);preparar(m,MaestroForm.desde(x),id);return"admin/maestro-form";}
    @PostMapping("/{id}") String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form") MaestroForm f,BindingResult e,Model m,RedirectAttributes flash){autorizado(id);if(e.hasErrors()){preparar(m,f,id);return"admin/maestro-form";}try{service.actualizar(id,f.request());flash.addFlashAttribute("mensaje","Expediente del maestro actualizado");return"redirect:/admin/maestros/"+id+"/editar";}catch(RuntimeException ex){return error(m,f,id,ex);}}
    @PostMapping("/{id}/desactivar") String desactivar(@PathVariable Long id,@RequestParam Long version,RedirectAttributes f){autorizado(id);service.desactivar(id,version);f.addFlashAttribute("mensaje","Maestro y asignaciones desactivados; el historial se conserva");return"redirect:/admin/catalogos/maestros";}
    @PostMapping("/{id}/fotografia") String fotografia(@PathVariable Long id,@RequestParam("archivo") MultipartFile archivo,RedirectAttributes f){autorizado(id);try{fotografias.asignar(id,archivo);f.addFlashAttribute("mensaje","Fotografía del maestro actualizada");}catch(RuntimeException e){f.addFlashAttribute("errorFotografia",MensajeErrorFormulario.desde(e));}return"redirect:/admin/maestros/"+id+"/editar";}
    @PostMapping("/{id}/fotografia/retirar") String retirarFotografia(@PathVariable Long id,RedirectAttributes f){autorizado(id);try{fotografias.retirar(id);f.addFlashAttribute("mensaje","Fotografía retirada de la ficha vigente");}catch(RuntimeException e){f.addFlashAttribute("errorFotografia",MensajeErrorFormulario.desde(e));}return"redirect:/admin/maestros/"+id+"/editar";}
    @GetMapping("/{id}/fotografia") ResponseEntity<Resource> verFotografia(@PathVariable Long id){autorizado(id);var a=fotografias.descargar(id);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.parseMediaType(a.tipoMime())).contentLength(a.tamanoBytes()).header(HttpHeaders.CONTENT_DISPOSITION,ContentDisposition.inline().filename(a.nombreOriginal(),StandardCharsets.UTF_8).build().toString()).body(a.recurso());}
    @PostMapping("/{id}/asignaciones") String asignar(@PathVariable Long id,@Valid @ModelAttribute("asignacionForm") AsignacionMaestroForm f,BindingResult e,Model m,RedirectAttributes flash){MaestroResponse x=autorizado(id);if(e.hasErrors()){preparar(m,MaestroForm.desde(x),id);m.addAttribute("asignacionForm",f);return"admin/maestro-form";}try{service.asignar(id,f.request());flash.addFlashAttribute("mensaje","Grupo y materia asignados correctamente");return"redirect:/admin/maestros/"+id+"/editar#asignaciones";}catch(RuntimeException ex){preparar(m,MaestroForm.desde(x),id);m.addAttribute("asignacionForm",f);m.addAttribute("errorAsignacion",MensajeErrorFormulario.desde(ex));return"admin/maestro-form";}}
    @PostMapping("/{id}/asignaciones/{asignacionId}/desactivar") String desactivarAsignacion(@PathVariable Long id,@PathVariable Long asignacionId,@RequestParam Long version,RedirectAttributes f){autorizado(id);service.desactivarAsignacion(id,asignacionId,version);f.addFlashAttribute("mensaje","Asignación finalizada; no se eliminó su historial");return"redirect:/admin/maestros/"+id+"/editar#asignaciones";}
    @PostMapping("/{id}/cuenta") String cuenta(@PathVariable Long id,@Valid @ModelAttribute("cuentaForm") PortalMaestroCuentaForm f,BindingResult e,Model m,RedirectAttributes flash){MaestroResponse x=autorizado(id);if(e.hasErrors()){preparar(m,MaestroForm.desde(x),id);m.addAttribute("cuentaForm",f);return"admin/maestro-form";}try{var c=acceso.crear(id,f.request());var inv=invitaciones.emitir(c.usuarioId(),Duration.ofHours(48));flash.addFlashAttribute("invitacionEnlace",enlaceActivacion(inv.token()));flash.addFlashAttribute("invitacionExpira",inv.expiraEn());flash.addFlashAttribute("mensaje","Cuenta docente creada. Copia el enlace de activación antes de salir");return"redirect:/admin/maestros/"+id+"/editar";}catch(RuntimeException ex){preparar(m,MaestroForm.desde(x),id);m.addAttribute("cuentaForm",f);m.addAttribute("errorCuenta",MensajeErrorFormulario.desde(ex));return"admin/maestro-form";}}
    @PostMapping("/{id}/cuenta/invitacion") String invitacion(@PathVariable Long id,RedirectAttributes f){autorizado(id);var c=acceso.obtener(id).orElseThrow(()->new ReglaNegocioException("El maestro no tiene una cuenta"));var x=invitaciones.emitir(c.usuarioId(),Duration.ofHours(48));f.addFlashAttribute("invitacionEnlace",enlaceActivacion(x.token()));f.addFlashAttribute("invitacionExpira",x.expiraEn());return"redirect:/admin/maestros/"+id+"/editar";}
    @PostMapping("/{id}/cuenta/recuperacion") String recuperacion(@PathVariable Long id,RedirectAttributes f){autorizado(id);var c=acceso.obtener(id).orElseThrow(()->new ReglaNegocioException("El maestro no tiene una cuenta"));var x=recuperaciones.emitir(c.usuarioId(),Duration.ofMinutes(30));String url=ServletUriComponentsBuilder.fromCurrentContextPath().path("/restablecer-password").queryParam("token",x.token()).build().toUriString();f.addFlashAttribute("recuperacionEnlace",url);f.addFlashAttribute("recuperacionExpira",x.expiraEn());return"redirect:/admin/maestros/"+id+"/editar";}
    @PostMapping("/{id}/cuenta/estado") String cuentaEstado(@PathVariable Long id,@RequestParam Long version,@RequestParam boolean activar,RedirectAttributes f){autorizado(id);acceso.cambiarDisponibilidad(id,version,activar);f.addFlashAttribute("mensaje",activar?"Acceso docente habilitado":"Acceso docente desactivado");return"redirect:/admin/maestros/"+id+"/editar";}
    private MaestroResponse autorizado(Long id){alcance.validarRecurso(ModuloCatalogo.MAESTROS,id);return service.obtener(id);}
    private void validarInstitucion(Long id){if(id!=null)alcance.validarAdministracionInstitucional(id);}
    private void preparar(Model m,MaestroForm f,Long id){var lista=alcance.filtrarInstituciones(instituciones.listar());if(f.getInstitucionId()==null&&lista.size()==1)f.setInstitucionId(lista.getFirst().id());m.addAttribute("form",f);m.addAttribute("id",id);m.addAttribute("edicion",id!=null);m.addAttribute("instituciones",lista);if(id!=null){var maestro=service.obtener(id);m.addAttribute("maestro",maestro);m.addAttribute("fotografiaActual",fotografias.actual(id));m.addAttribute("asignaciones",service.asignaciones(id));m.addAttribute("cuenta",acceso.obtener(id).orElse(null));m.addAttribute("asignacionForm",m.containsAttribute("asignacionForm")?m.getAttribute("asignacionForm"):new AsignacionMaestroForm());if(!m.containsAttribute("cuentaForm")){var cf=new PortalMaestroCuentaForm();cf.setUsername(acceso.sugerirUsername(id));cf.setEmail(maestro.email());m.addAttribute("cuentaForm",cf);}}}
    private String error(Model m,MaestroForm f,Long id,RuntimeException ex){preparar(m,f,id);m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return"admin/maestro-form";}
    private String enlaceActivacion(String token){return ServletUriComponentsBuilder.fromCurrentContextPath().path("/activar-cuenta").queryParam("token",token).build().toUriString();}
}
