package escuela.personal.controller;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import escuela.admin.support.MensajeErrorFormulario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/preparacion-nomina/pago-individual")
public class PagoIndividualNominaController {
 private final PagoMultipleNominaService service;
 private final PreparacionNominaService preparacion;
 private final EmpleadoService empleados;
 private static final String RUTA="/admin/preparacion-nomina/pago-individual";
 private String pantalla(PagoIndividualNominaForm f,FiltroNomina filtro,Model m){
  empleados.autorizarSalarios(true);m.addAttribute("filtro",filtro.normalizado());m.addAttribute("formMultiple",f);m.addAttribute("individualDirecto",true);m.addAttribute("rutaMultiple",RUTA);m.addAttribute("volverUrl",filtro.url("/admin/preparacion-nomina"));m.addAttribute("tituloReferencia","Pago individual · un empleado y un periodo");m.addAttribute("cuentasEndpoint",RUTA+"/cuentas");m.addAttribute("motivosEndpoint",RUTA+"/motivos");
  if(f.getEmpleadoId()!=null&&f.getPeriodoId()!=null)try{var c=service.calculoIndividual(f);m.addAttribute("totalSolicitado",c.propuesto());m.addAttribute("empleadoIndividual",c);m.addAttribute("lineasIndividuales",preparacion.contexto(f.getPeriodoId()).periodo());}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());}
  return "admin/nomina-pago-multiple";
 }
 @GetMapping String nuevo(@ModelAttribute("formMultiple")PagoIndividualNominaForm f,@ModelAttribute FiltroNomina filtro,Model m){return pantalla(f,filtro,m);}
 @PostMapping("/revisar") String revisar(@ModelAttribute("formMultiple")PagoIndividualNominaForm f,@ModelAttribute FiltroNomina filtro,Model m){try{var v=service.revisarIndividual(f);f.setSello(v.sello());m.addAttribute("vistaMultiple",v);}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());}return pantalla(f,filtro,m);}
 @PostMapping("/agregar-fuente") String agregar(@ModelAttribute("formMultiple")PagoIndividualNominaForm f,@ModelAttribute FiltroNomina filtro,Model m){if(f.getFuentes()!=null&&f.getFuentes().size()<20)f.getFuentes().add(new PagoMultipleNominaForm.Fuente());else m.addAttribute("errorOperacion","Puedes usar un máximo de veinte fuentes.");return pantalla(f,filtro,m);}
 @PostMapping("/confirmar") String confirmar(@ModelAttribute("formMultiple")PagoIndividualNominaForm f,@ModelAttribute FiltroNomina filtro,Model m,RedirectAttributes flash){try{Long id=service.pagarIndividual(f);flash.addFlashAttribute("mensaje","Pago individual registrado. Su cobertura no podrá pagarse de nuevo en una nómina colectiva.");return "redirect:"+filtro.url("/admin/preparacion-nomina/"+id+"/pagos");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));return pantalla(f,filtro,m);}}
 @GetMapping("/cuentas") @ResponseBody ResponseEntity<ResultadoAutocompletado> cuentas(@RequestParam(defaultValue="")String q){var r=service.buscarCuentas(q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
 @GetMapping("/motivos") @ResponseBody ResponseEntity<ResultadoAutocompletado> motivos(@RequestParam(defaultValue="")String q){var r=service.buscarMotivos(q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
}
