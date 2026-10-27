package escuela.personal.controller;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller @RequiredArgsConstructor
public class CondicionesSalarialesController {
 private final CondicionesSalarialesService service;
 private final EmpleadoService empleados;
 private final ExcelSalarialService excel;
 private boolean escritura(){return empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR");}
 @GetMapping("/admin/empleados/{id}/condiciones")
 String condiciones(@PathVariable Long id,@ModelAttribute("filtro")FiltroSalarial filtro,
  @RequestParam(required=false) @org.springframework.format.annotation.DateTimeFormat(iso=org.springframework.format.annotation.DateTimeFormat.ISO.DATE)LocalDate fechaConsulta,
  @RequestParam(defaultValue="0")int paginaHistorial,Model m){preparar(id,filtro,fechaConsulta,paginaHistorial,m);return "admin/empleado-condiciones";}
 private void preparar(Long id,FiltroSalarial filtro,LocalDate fecha,int paginaHistorial,Model m){
  empleados.autorizarSalarios(false);m.addAttribute("empleado",empleados.obtener(id));m.addAttribute("id",id);m.addAttribute("puedeEditar",escritura());
  var f=filtro.normalizado();m.addAttribute("filtro",f);var selecciones=new FiltrosCatalogosPersonal();selecciones.setFiltroPuestoId(f.filtroPuestoId());selecciones.setFiltroPuestoTexto(f.filtroPuestoTexto());if(f.filtroPuestoId()!=null){var p=service.puesto(f.filtroPuestoId());selecciones.setFiltroPuestoTexto(p.codigo()+" · "+p.nombre());}m.addAttribute("selecciones",selecciones);var hoy=empleados.hoyLaboral(id);m.addAttribute("hoy",hoy);
  m.addAttribute("fechaConsulta",fecha==null?hoy:fecha);m.addAttribute("aplicable",service.aplicable(id,fecha==null?hoy:fecha));
  try{m.addAttribute("resultado",service.listar(id,f));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}
  m.addAttribute("historial",service.historial(id,paginaHistorial));
  m.addAttribute("laboral",empleados.laboral(id));
  m.addAttribute("moneda",service.moneda(id));
  if(!m.containsAttribute("form")){var form=new CondicionSalarialForm();form.setFechaInicio(hoy);form.setVersionLaboral(empleados.laboral(id).getVersion());form.setRevision(service.revision(id));m.addAttribute("form",form);}
 }
 @PostMapping("/admin/empleados/{id}/condiciones")
 String programar(@PathVariable Long id,@Valid @ModelAttribute("form")CondicionSalarialForm form,BindingResult errores,
  @ModelAttribute("filtro")FiltroSalarial filtro,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);empleados.autorizar(id,true,false);
  if(!errores.hasErrors())try{service.programar(id,form);flash.addFlashAttribute("mensaje","Condición salarial registrada. No se generaron nóminas ni movimientos de dinero.");return "redirect:/admin/empleados/"+id+"/condiciones";}
  catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  preparar(id,filtro,null,0,m);m.addAttribute("pestanaActiva","captura");return "admin/empleado-condiciones";
 }
 @PostMapping("/admin/empleados/{id}/condiciones/{condicionId}/cancelar")
 String cancelar(@PathVariable Long id,@PathVariable Long condicionId,@RequestParam Long version,@RequestParam String motivo,RedirectAttributes flash){
  try{service.cancelar(id,condicionId,version,motivo);flash.addFlashAttribute("mensaje","Programación cancelada. Se restauró la vigencia anterior cuando correspondía, sin movimientos financieros.");}
  catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){flash.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  return "redirect:/admin/empleados/"+id+"/condiciones";
 }
 @GetMapping("/admin/empleados/{id}/condiciones/excel")
 void excel(@PathVariable Long id,@ModelAttribute FiltroSalarial filtro,HttpServletResponse r)throws IOException{
  service.listar(id,filtro.pagina(0,10));cabecera(r,"condiciones-salariales.xlsx");excel.condiciones(id,filtro,r.getOutputStream());
 }
 @GetMapping("/admin/puestos/buscar") @ResponseBody
 ResponseEntity<ResultadoAutocompletado> buscar(@RequestParam(defaultValue="")String q){var filas=service.buscarPuestos(q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).map(p->new OpcionAutocompletado(p.id(),p.codigo()+" · "+p.nombre(),p.completo()?p.horasSemanales()+" h/semana · $"+p.sueldoMensual()+" mensuales":"Pendiente de completar horas y sueldo")).toList(),filas.size()>10));}
 @GetMapping("/admin/puestos")
 String puestos(@RequestParam(required=false)Long id,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="TODOS")String estado,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,@ModelAttribute("selecciones")FiltrosCatalogosPersonal selecciones,Model m){
  empleados.autorizarSalarios(false);
  if(id!=null)return "redirect:"+urlPuestos("/admin/puestos/"+id+"/editar",q,estado,pagina,tamanio,selecciones);
  prepararFiltroPuestos(q,estado,pagina,tamanio,selecciones,m);
  try{selecciones.validar();m.addAttribute("resultado",selecciones.getFiltroPuestoId()==null?service.puestos((String)m.getAttribute("q"),(String)m.getAttribute("estado"),(Integer)m.getAttribute("pagina"),(Integer)m.getAttribute("tamanio")):service.puestos((String)m.getAttribute("q"),(String)m.getAttribute("estado"),(Integer)m.getAttribute("pagina"),(Integer)m.getAttribute("tamanio"),selecciones.getFiltroPuestoId()));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}
  var principal=empleados.autorizarSalarios(false);var permisos=principal.authorities().stream().map(a->a.getAuthority()).collect(java.util.stream.Collectors.toSet());
  m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(permisos)).toList());m.addAttribute("moduloActual",ModuloCatalogo.PUESTOS);
  return "admin/puestos";
 }
 @GetMapping("/admin/puestos/nuevo")
 String nuevoPuesto(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="TODOS")String estado,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,@ModelAttribute("selecciones")FiltrosCatalogosPersonal selecciones,Model m){
  empleados.autorizarSalarios(true);prepararFiltroPuestos(q,estado,pagina,tamanio,selecciones,m);m.addAttribute("form",new PuestoForm());return "admin/puesto-form";
 }
 @GetMapping("/admin/puestos/{id}/editar")
 String editarPuesto(@PathVariable Long id,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="TODOS")String estado,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,@ModelAttribute("selecciones")FiltrosCatalogosPersonal selecciones,Model m){
  empleados.autorizarSalarios(true);var p=service.puesto(id);var f=new PuestoForm();f.setCodigo(p.codigo());f.setNombre(p.nombre());f.setDescripcion(p.descripcion());f.setActivo(p.activo());f.setVersion(p.version());f.setHorasSemanales(p.horasSemanales());f.setSueldoMensual(p.sueldoMensual());
  prepararFiltroPuestos(q,estado,pagina,tamanio,selecciones,m);m.addAttribute("form",f);m.addAttribute("puestoId",id);return "admin/puesto-form";
 }
 private void prepararFiltroPuestos(String q,String estado,int pagina,int tamanio,FiltrosCatalogosPersonal selecciones,Model m){
  empleados.autorizarSalarios(false);m.addAttribute("selecciones",selecciones);
  if(selecciones.getFiltroPuestoId()!=null){var p=service.puesto(selecciones.getFiltroPuestoId());selecciones.setFiltroPuestoTexto(p.codigo()+" · "+p.nombre());}
  String texto=q==null?"":q.strip();m.addAttribute("q",texto.substring(0,Math.min(texto.length(),200)));m.addAttribute("estado",Set.of("ACTIVO","INACTIVO").contains(estado==null?"":estado)?estado:"TODOS");
  m.addAttribute("pagina",Math.max(0,pagina));m.addAttribute("tamanio",Set.of(10,25,50,100).contains(tamanio)?tamanio:25);m.addAttribute("puedeEditar",escritura());
  m.addAttribute("volverUrl",urlPuestos("/admin/puestos",(String)m.getAttribute("q"),(String)m.getAttribute("estado"),(Integer)m.getAttribute("pagina"),(Integer)m.getAttribute("tamanio"),selecciones));
 }
 private String urlPuestos(String ruta,String q,String estado,int pagina,int tamanio,FiltrosCatalogosPersonal selecciones){return selecciones.agregarA(org.springframework.web.util.UriComponentsBuilder.fromPath(ruta).queryParam("q",q==null?"":q.strip().substring(0,Math.min(q.strip().length(),200)))
  .queryParam("estado",Set.of("ACTIVO","INACTIVO").contains(estado==null?"":estado)?estado:"TODOS").queryParam("pagina",Math.max(0,pagina)).queryParam("tamanio",Set.of(10,25,50,100).contains(tamanio)?tamanio:25)).build().encode().toUriString();}
 @PostMapping("/admin/puestos")
 String guardarPuesto(@RequestParam(required=false)Long id,@Valid @ModelAttribute("form")PuestoForm form,BindingResult errores,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="TODOS")String estado,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,@ModelAttribute("selecciones")FiltrosCatalogosPersonal selecciones,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);if(id!=null)service.puesto(id);
  if(!errores.hasErrors())try{service.guardarPuesto(id,form);flash.addFlashAttribute("mensaje","Puesto guardado. Las condiciones salariales anteriores conservan su nombre histórico.");return "redirect:"+urlPuestos("/admin/puestos",q,estado,pagina,tamanio,selecciones);}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  prepararFiltroPuestos(q,estado,pagina,tamanio,selecciones,m);m.addAttribute("puestoId",id);return "admin/puesto-form";
 }
 @GetMapping("/admin/puestos/excel")
 void excelPuestos(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="TODOS")String estado,@ModelAttribute("selecciones")FiltrosCatalogosPersonal selecciones,HttpServletResponse r)throws IOException{selecciones.validar();service.puestos(q,estado,0,10,selecciones.getFiltroPuestoId());cabecera(r,"puestos.xlsx");excel.puestos(q,estado,selecciones.getFiltroPuestoId(),r.getOutputStream());}
 private void cabecera(HttpServletResponse r,String nombre){r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename="+nombre);r.setHeader("Cache-Control","no-store");}
}
