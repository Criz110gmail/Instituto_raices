package escuela.personal.controller;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.admin.support.MensajeErrorFormulario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/preparacion-nomina/{idNomina}/pago-multiple")
public class PagoMultipleNominaController {
 private final PagoMultipleNominaService service;
 private final PagoEmpleadoNominaService pagos;
 private final PreparacionNominaService preparacion;
 private String ruta(Long id){return "/admin/preparacion-nomina/"+id+"/pago-multiple";}
 private String pantalla(Long id,FiltroNomina filtro,Model m){
  if(!pagos.puedePagar())throw new org.springframework.security.access.AccessDeniedException("Falta autorización financiera");
  m.addAttribute("borrador",preparacion.obtener(id));m.addAttribute("filtro",filtro.normalizado());m.addAttribute("rutaMultiple",ruta(id));
  m.addAttribute("tituloReferencia",preparacion.obtener(id).folio()+" · "+preparacion.obtener(id).periodoCodigo());m.addAttribute("cuentasEndpoint","/admin/preparacion-nomina/"+id+"/pagos/cuentas");m.addAttribute("motivosEndpoint","/admin/preparacion-nomina/"+id+"/pagos/motivos");var form=(PagoMultipleNominaForm)m.getAttribute("formMultiple");m.addAttribute("totalSolicitado",form!=null&&form.getFilaId()!=null?pagos.fila(id,form.getFilaId()).neto():service.totalColectivo(id));
  m.addAttribute("volverUrl",filtro.url("/admin/preparacion-nomina/"+id+"/pagos"));return "admin/nomina-pago-multiple";
 }
 @GetMapping String nuevo(@PathVariable("idNomina")Long id,@RequestParam(required=false)Long filaId,@ModelAttribute FiltroNomina filtro,Model m){
  var f=new PagoMultipleNominaForm();f.setVersion(preparacion.obtener(id).version());f.setFilaId(filaId);
  if(filaId!=null)pagos.fila(id,filaId);m.addAttribute("formMultiple",f);return pantalla(id,filtro,m);
 }
 @PostMapping("/revisar") String revisar(@PathVariable("idNomina")Long id,@ModelAttribute("formMultiple")PagoMultipleNominaForm f,@ModelAttribute FiltroNomina filtro,Model m){
  try{var vista=service.revisar(id,f);f.setSello(vista.sello());m.addAttribute("vistaMultiple",vista);}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());}return pantalla(id,filtro,m);
 }
 @PostMapping("/agregar-fuente") String agregar(@PathVariable("idNomina")Long id,@ModelAttribute("formMultiple")PagoMultipleNominaForm f,@ModelAttribute FiltroNomina filtro,Model m){
  if(!f.agregarFuente())m.addAttribute("errorOperacion","Puedes distribuir el pago entre una y veinte cuentas.");return pantalla(id,filtro,m);
 }
 @PostMapping("/quitar-fuente") String quitar(@PathVariable("idNomina")Long id,@RequestParam int indiceFuente,@ModelAttribute("formMultiple")PagoMultipleNominaForm f,@ModelAttribute FiltroNomina filtro,Model m){
  if(!f.quitarFuente(indiceFuente))m.addAttribute("errorOperacion","Conserva al menos una cuenta y selecciona una fuente válida para quitar.");return pantalla(id,filtro,m);
 }
 @PostMapping("/confirmar") String confirmar(@PathVariable("idNomina")Long id,@ModelAttribute("formMultiple")PagoMultipleNominaForm f,@ModelAttribute FiltroNomina filtro,Model m,RedirectAttributes flash){
  try{service.pagar(id,f);flash.addFlashAttribute("mensaje","Nómina registrada: cada pago conserva sus cuentas de origen. No se enviaron transferencias al banco.");return "redirect:"+filtro.url("/admin/preparacion-nomina/"+id+"/pagos");}
  catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));return pantalla(id,filtro,m);}
 }
}
