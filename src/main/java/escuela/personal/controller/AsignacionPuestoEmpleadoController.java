package escuela.personal.controller;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/empleados/{id}/puesto")
public class AsignacionPuestoEmpleadoController {
 private final EmpleadoService empleados;
 private final CondicionesSalarialesService salarios;
 private final OrganizacionLaboralService organizacion;
 @GetMapping String detalle(@PathVariable Long id,
  @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate fecha,
  @RequestParam(defaultValue="0")int pagina,Model m){
  base(id,m);var consulta=fecha==null?empleados.hoyLaboral(id):fecha;m.addAttribute("fecha",consulta);
  var actual=salarios.aplicable(id,consulta);m.addAttribute("asignacion",actual==null?null:organizacion.asignacion(actual.id()));
  m.addAttribute("pagina",Math.max(0,pagina));m.addAttribute("historial",organizacion.asignaciones(id,"","TODAS",null,null,pagina,10));return "admin/empleado-puesto";
 }
 @GetMapping("/nuevo") String nuevo(@PathVariable Long id,@RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE)LocalDate consulta,@RequestParam(defaultValue="0")int pagina,Model m){
  empleados.autorizarSalarios(true);base(id,m);var f=new AsignacionPuestoForm();f.setInicio(empleados.hoyLaboral(id));
  f.setConsulta(consulta);f.setPagina(Math.max(0,pagina));f.setRevision(salarios.revision(id));f.setVersionLaboral(empleados.laboral(id).getVersion());m.addAttribute("form",f);retorno(id,f,m);return "admin/empleado-puesto-form";
 }
 @GetMapping("/contexto") @ResponseBody ResponseEntity<?> contexto(@PathVariable Long id,@RequestParam Long puestoId){
  empleados.autorizarSalarios(true);empleados.autorizar(id,true,false);var p=salarios.puesto(puestoId);
  if(!p.activo()||!p.completo())throw new ReglaNegocioException("Completa horas y sueldo mensual en Puestos laborales y activa el puesto antes de asignarlo.");
  return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(java.util.Map.of("puesto",p,"moneda",salarios.moneda(id)));
 }
 @PostMapping String guardar(@PathVariable Long id,@Valid @ModelAttribute("form")AsignacionPuestoForm f,BindingResult errores,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);empleados.autorizar(id,true,false);
  if(!errores.hasErrors())try{salarios.asignarPuesto(id,f.condicion(),f.getPuestoVersion());flash.addFlashAttribute("mensaje","Puesto y sueldo mensual asignados. La vigencia anterior se conserva; no se generó nómina ni se movió dinero.");return "redirect:/admin/empleados/"+id+"/puesto?fecha="+f.getInicio();}
  catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}
  base(id,m);retorno(id,f,m);return "admin/empleado-puesto-form";
 }
 private void retorno(Long id,AsignacionPuestoForm f,Model m){m.addAttribute("volverUrl","/admin/empleados/"+id+"/puesto?pagina="+Math.max(0,f.getPagina())+(f.getConsulta()==null?"":"&fecha="+f.getConsulta()));}
 private void base(Long id,Model m){empleados.autorizarSalarios(false);m.addAttribute("empleado",empleados.obtener(id));m.addAttribute("id",id);m.addAttribute("hoy",empleados.hoyLaboral(id));m.addAttribute("puedeEditar",empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));}
}
