package escuela.personal.controller;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/conceptos-empleado")
public class ConceptoEmpleadoController {
 private final ConceptoEmpleadoService service;
 private final ConceptoNominaService catalogo;
 private final EmpleadoService empleados;
 private void base(FiltroConceptoEmpleado original,Model m){
  var p=empleados.autorizarSalarios(false);var f=original.normalizado();m.addAttribute("filtro",f);m.addAttribute("volverUrl",f.url("/admin/conceptos-empleado"));m.addAttribute("puedeEditar",empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));m.addAttribute("hoy",service.hoy());
  m.addAttribute("empleadoFiltro",f.filtroEmpleadoTexto());if(f.filtroEmpleadoId()!=null){var e=empleados.obtener(f.filtroEmpleadoId());m.addAttribute("empleadoFiltro",e.codigo()+" · "+e.nombre());}
  m.addAttribute("conceptoFiltro",f.filtroConceptoTexto());if(f.filtroConceptoId()!=null){var c=catalogo.obtener(f.filtroConceptoId());m.addAttribute("conceptoFiltro",c.codigo()+" · "+c.nombre());}
  var permisos=p.authorities().stream().map(x->x.getAuthority()).collect(java.util.stream.Collectors.toSet());m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(permisos)).toList());m.addAttribute("moduloActual",ModuloCatalogo.CONCEPTOS_EMPLEADO);
 }
 @GetMapping String listado(@ModelAttribute FiltroConceptoEmpleado filtro,Model m){base(filtro,m);try{m.addAttribute("resultado",service.listar(filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}return "admin/conceptos-empleado";}
 @GetMapping("/nuevo") String nuevo(@ModelAttribute FiltroConceptoEmpleado filtro,Model m){empleados.autorizarSalarios(true);base(filtro,m);var f=new ConceptoEmpleadoForm();f.setInicio(service.hoy());m.addAttribute("form",f);return "admin/concepto-empleado-form";}
 @GetMapping("/{id:\\d+}/cambio") String cambio(@PathVariable Long id,@ModelAttribute FiltroConceptoEmpleado filtro,Model m){
  empleados.autorizarSalarios(true);var a=service.obtener(id);base(filtro,m);var f=new ConceptoEmpleadoForm();f.setEmpleadoId(a.empleadoId());f.setEmpleadoTexto(a.codigoEmpleado()+" · "+a.empleado());f.setConceptoId(a.conceptoId());var c=catalogo.obtener(a.conceptoId());f.setConceptoTexto(c.codigo()+" · "+c.nombre());f.setImporte(a.importe());f.setInicio(service.hoy().plusDays(1).isAfter(a.inicio())?service.hoy().plusDays(1):a.inicio().plusDays(1));f.setAnteriorId(id);f.setAnteriorVersion(a.version());m.addAttribute("anterior",a);m.addAttribute("form",f);return "admin/concepto-empleado-form";
 }
 @PostMapping String asignar(@Valid @ModelAttribute("form")ConceptoEmpleadoForm form,BindingResult errores,@ModelAttribute FiltroConceptoEmpleado filtro,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);if(form.getAnteriorId()!=null)service.obtener(form.getAnteriorId());
  if(!errores.hasErrors())try{Long id=service.asignar(form);flash.addFlashAttribute("mensaje","Concepto asignado con importe y vigencia históricos. No se generó nómina ni se movió dinero.");return "redirect:"+filtro.url("/admin/conceptos-empleado/"+id);}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  if(errores.hasErrors())m.addAttribute("errorOperacion","Completa los campos marcados, selecciona empleado y concepto, y espera a que termine su consulta.");base(filtro,m);if(form.getAnteriorId()!=null)m.addAttribute("anterior",service.obtener(form.getAnteriorId()));return "admin/concepto-empleado-form";
 }
 @GetMapping("/{id:\\d+}") String detalle(@PathVariable Long id,@ModelAttribute FiltroConceptoEmpleado filtro,@RequestParam(defaultValue="0")int paginaHistorial,Model m){base(filtro,m);m.addAttribute("asignacion",service.obtener(id));m.addAttribute("historial",service.historial(id,paginaHistorial));return "admin/concepto-empleado-detalle";}
 @PostMapping("/{id:\\d+}/{operacion:cancelar|terminar}") String gestionar(@PathVariable Long id,@PathVariable String operacion,@RequestParam Long version,@RequestParam String motivo,@RequestParam(required=false)@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate fin,@ModelAttribute FiltroConceptoEmpleado filtro,Model m){
  empleados.autorizarSalarios(true);try{service.gestionar(id,version,motivo,fin,operacion.equals("cancelar"));m.addAttribute("mensaje",operacion.equals("cancelar")?"Programación cancelada; se restauró la vigencia anterior cuando corresponde. Sin movimientos financieros.":"Fin de vigencia registrado; se conserva el historial. No se generaron pagos.");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));m.addAttribute("motivoCapturado",motivo);m.addAttribute("finCapturado",fin);}
  m.addAttribute("tabInicial","gestion");return detalle(id,filtro,0,m);
 }
 @GetMapping("/empleados/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> empleados(@RequestParam(defaultValue="")String q){return opciones(service.buscarEmpleados(q));}
 @GetMapping("/conceptos/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> conceptos(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="false")boolean captura){return opciones(service.buscarConceptos(q,captura));}
 private ResponseEntity<ResultadoAutocompletado> opciones(List<OpcionAutocompletado> filas){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/contexto") @ResponseBody ResponseEntity<ConceptoEmpleadoService.Contexto> contexto(@RequestParam Long empleadoId,@RequestParam Long conceptoId){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.contexto(empleadoId,conceptoId));}
 @GetMapping("/excel") void excel(@ModelAttribute FiltroConceptoEmpleado original,HttpServletResponse response)throws IOException{
  var f=original.normalizado();service.listar(f.pagina(0,100));response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=conceptos-empleado.xlsx");response.setHeader("Cache-Control","no-store");
  try(var book=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var sheet=book.createSheet("Conceptos por empleado");String[] cab={"Código laboral","Empleado","Código histórico","Concepto histórico","Tipo","Efecto","Aplicación","Importe fijo por nómina","Moneda","Inicio","Fin","Estado","Motivo de registro","Última gestión"};var h=sheet.createRow(0);for(int i=0;i<cab.length;i++){h.createCell(i).setCellValue(cab[i]);sheet.setColumnWidth(i,30*256);}int fila=1,pagina=0;org.springframework.data.domain.Page<ConceptoEmpleadoService.Asignacion> bloque;
   do{bloque=service.listar(f.pagina(pagina++,100));for(var a:bloque){var row=sheet.createRow(fila++);Object[] valores={a.codigoEmpleado(),a.empleado(),a.codigo(),a.nombre(),a.tipoTexto(),a.efecto(),"Habitual",a.importe(),a.moneda(),fecha(a.inicio()),fecha(a.fin()),a.estado(),a.motivo(),a.motivoOperacion()};for(int i=0;i<valores.length;i++){var cell=row.createCell(i);if(valores[i] instanceof Number n)cell.setCellValue(n.doubleValue());else cell.setCellValue(Objects.toString(valores[i],""));}}}while(bloque.hasNext());
   sheet.createFreezePane(0,1);sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,cab.length-1));book.write(response.getOutputStream());
  }finally{book.dispose();}}
 }
 private static String fecha(LocalDate fecha){return fecha==null?"Sin fecha de fin":fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
}
