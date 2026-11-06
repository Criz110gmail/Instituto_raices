package escuela.personal.controller;
import escuela.personal.service.*;
import escuela.personal.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/empleados/{id}/portal-recibos")
public class AccesoRecibosEmpleadoController {
 private final AccesoRecibosEmpleadoService service;
 private final EmpleadoService personal;
 @GetMapping String ver(@PathVariable Long id,Model m,HttpServletResponse r){preparar(id,m,r);if(!m.containsAttribute("form")){var c=service.obtener(id);var f=new AccesoRecibosForm();f.setUsername(c.username());f.setEmail(c.email());f.setVersion(c.version());m.addAttribute("form",f);}return "admin/empleado-acceso-recibos";}
 @PostMapping String guardar(@PathVariable Long id,@Valid @ModelAttribute("form")AccesoRecibosForm f,BindingResult errores,Model m,RedirectAttributes flash,HttpServletResponse r){
  personal.autorizarSalarios(true);personal.autorizar(id,false,true);
  if(!errores.hasErrors())try{var enlace=service.gestionar(id,f);if(enlace!=null){String ruta=enlace.activacion()?"/activar-cuenta":"/restablecer-password";flash.addFlashAttribute("enlaceAcceso",ServletUriComponentsBuilder.fromCurrentContextPath().path(ruta).queryParam("token",enlace.token()).build().toUriString());flash.addFlashAttribute("enlaceExpira",java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(java.time.ZoneId.of(personal.autorizar(id,false,true).getInstitucion().getZonaHoraria())).format(enlace.expira()));}flash.addFlashAttribute("mensaje",f.getAccion().equals("DESHABILITAR")?"Acceso a recibos desactivado. Su historial se conserva.":"Acceso actualizado. No se enviaron correos ni se modificó la nómina.");return "redirect:/admin/empleados/"+id+"/portal-recibos";}
  catch(escuela.common.exception.ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException|org.springframework.orm.ObjectOptimisticLockingFailureException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  preparar(id,m,r);return "admin/empleado-acceso-recibos";
 }
 private void preparar(Long id,Model m,HttpServletResponse r){r.setHeader("Cache-Control","no-store");m.addAttribute("cuenta",service.obtener(id));m.addAttribute("puedeEditar",personal.permitido("EMPLEADO_ADMINISTRAR")&&personal.permitido("CONDICION_SALARIAL_ADMINISTRAR"));m.addAttribute("puedeMaestro",personal.permitido("MAESTRO_ADMINISTRAR"));}
}
