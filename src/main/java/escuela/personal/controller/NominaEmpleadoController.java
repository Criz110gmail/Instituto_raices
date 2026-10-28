package escuela.personal.controller;

import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import java.util.*;
import java.io.IOException;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/empleados/{empleadoId}/nomina")
public class NominaEmpleadoController {
 private final EmpleadoService empleados;
 private final NominaEmpleadoService service;
 private final ConceptoEmpleadoService conceptos;
 private final CondicionesSalarialesService salarios;
 private final ConceptoNominaService catalogo;
 private void base(Long id,FiltroNominaEmpleado filtro,Model m){empleados.autorizarNomina(false);m.addAttribute("empleado",empleados.obtener(id));m.addAttribute("id",id);m.addAttribute("filtroNomina",filtro.normalizado());m.addAttribute("conceptoFiltroNomina",filtro.filtroConceptoId()==null?filtro.filtroConceptoTexto():service.etiquetaConcepto(id,filtro.filtroConceptoId()));m.addAttribute("volverUrl",filtro.url(id,""));m.addAttribute("hoyNomina",conceptos.hoy());m.addAttribute("puedeEditarNomina",empleados.permitido("EMPLEADO_NOMINA_ADMINISTRAR")&&empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));}
 @GetMapping String listado(@PathVariable Long empleadoId,@ModelAttribute FiltroNominaEmpleado filtro,Model m){base(empleadoId,filtro,m);m.addAttribute("sueldoNomina",salarios.aplicable(empleadoId,conceptos.hoy()));try{m.addAttribute("conceptosNomina",service.listar(empleadoId,filtro));m.addAttribute("simulacionNomina",service.simular(empleadoId,filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("conceptosNomina",org.springframework.data.domain.Page.empty());}return "admin/empleado-nomina";}
 @GetMapping("/nuevo") String nuevo(@PathVariable Long empleadoId,@ModelAttribute FiltroNominaEmpleado filtro,Model m){empleados.autorizarNomina(true);base(empleadoId,filtro,m);var e=empleados.obtener(empleadoId);var f=new ConceptoEmpleadoForm();f.setEmpleadoId(empleadoId);f.setEmpleadoTexto(e.codigo()+" · "+e.nombre());f.setInicio(conceptos.hoy());m.addAttribute("form",f);return "admin/empleado-nomina-form";}
 @GetMapping("/{asignacionId}/cambio") String cambio(@PathVariable Long empleadoId,@PathVariable Long asignacionId,@ModelAttribute FiltroNominaEmpleado filtro,Model m){
  empleados.autorizarNomina(true);var a=service.obtener(empleadoId,asignacionId);var meta=service.metadatos(empleadoId,asignacionId);
  if(!a.modificable()||"UNICA".equals(meta.get("modalidad")))throw new ReglaNegocioException("Selecciona un concepto recurrente con vigencia actual o futura para programar un cambio.");
  base(empleadoId,filtro,m);var e=empleados.obtener(empleadoId);var f=new ConceptoEmpleadoForm();f.setEmpleadoId(empleadoId);f.setEmpleadoTexto(e.codigo()+" · "+e.nombre());f.setConceptoId(a.conceptoId());var vigente=catalogo.obtener(a.conceptoId());f.setConceptoTexto(vigente.codigo()+" · "+vigente.nombre());f.setImporte(a.importe());f.setAnteriorId(a.id());f.setAnteriorVersion(a.version());f.setFrecuenciaImporte(Objects.toString(meta.get("frecuencia_importe"),"MENSUAL"));m.addAttribute("anterior",a);m.addAttribute("form",f);return "admin/empleado-nomina-form";
 }
 @PostMapping String guardar(@PathVariable Long empleadoId,@Valid @ModelAttribute("form")ConceptoEmpleadoForm f,BindingResult errores,@ModelAttribute FiltroNominaEmpleado filtro,Model m,RedirectAttributes flash){
  empleados.autorizarNomina(true);if(!Objects.equals(empleadoId,f.getEmpleadoId()))throw new org.springframework.security.access.AccessDeniedException("Empleado no autorizado");
  if(f.getAnteriorId()!=null)service.obtener(empleadoId,f.getAnteriorId());
  if(!errores.hasErrors())try{service.guardar(empleadoId,f);flash.addFlashAttribute("mensaje","Configuración guardada con su frecuencia y vigencia. No se calculó nómina ni se movió dinero.");return "redirect:"+filtro.url(empleadoId,"");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}
  base(empleadoId,filtro,m);if(f.getAnteriorId()!=null)m.addAttribute("anterior",service.obtener(empleadoId,f.getAnteriorId()));return "admin/empleado-nomina-form";
 }
 @GetMapping("/{asignacionId}") String detalle(@PathVariable Long empleadoId,@PathVariable Long asignacionId,@ModelAttribute FiltroNominaEmpleado filtro,@RequestParam(defaultValue="0")int paginaHistorial,Model m){base(empleadoId,filtro,m);m.addAttribute("asignacion",service.obtener(empleadoId,asignacionId));m.addAttribute("meta",service.metadatos(empleadoId,asignacionId));m.addAttribute("historialConcepto",conceptos.historial(asignacionId,paginaHistorial));return "admin/empleado-nomina-detalle";}
 @PostMapping("/{asignacionId}/{accion:cancelar|terminar}") String gestionar(@PathVariable Long empleadoId,@PathVariable Long asignacionId,@PathVariable String accion,@RequestParam Long version,@RequestParam String motivo,@RequestParam(required=false)@DateTimeFormat(iso=DateTimeFormat.ISO.DATE)LocalDate fin,@ModelAttribute FiltroNominaEmpleado filtro,Model m,RedirectAttributes flash){
  empleados.autorizarNomina(true);try{service.gestionar(empleadoId,asignacionId,version,motivo,fin,accion.equals("cancelar"));flash.addFlashAttribute("mensaje","Gestión guardada. Se conserva el historial y no se modificaron nóminas ni cuentas.");return "redirect:"+filtro.url(empleadoId,"/"+asignacionId);}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));m.addAttribute("motivoCapturado",motivo);m.addAttribute("finCapturado",fin);return detalle(empleadoId,asignacionId,filtro,0,m);}
 }
 @GetMapping("/conceptos/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> buscar(@PathVariable Long empleadoId,@RequestParam(defaultValue="")String q){var filas=service.buscar(empleadoId,q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/periodos/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> periodos(@PathVariable Long empleadoId,@RequestParam(defaultValue="")String q){var filas=service.periodos(empleadoId,q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/conceptos-filtrar/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> filtrarConceptos(@PathVariable Long empleadoId,@RequestParam(defaultValue="")String q){var filas=service.filtrarConceptos(empleadoId,q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/contexto") @ResponseBody ResponseEntity<?> contexto(@PathVariable Long empleadoId,@RequestParam Long conceptoId){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(conceptos.contextoConfiguracion(empleadoId,conceptoId));}
 @GetMapping("/periodo") @ResponseBody ResponseEntity<?> periodo(@PathVariable Long empleadoId,@RequestParam Long periodoId){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.periodo(empleadoId,periodoId));}
 @GetMapping("/excel") void excel(@PathVariable Long empleadoId,@ModelAttribute FiltroNominaEmpleado filtro,HttpServletResponse response)throws IOException{
  service.listar(empleadoId,filtro.pagina(0,100));response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=configuracion-nomina-empleado.xlsx");response.setHeader("Cache-Control","no-store");
  try(var book=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var sheet=book.createSheet("Conceptos y vigencias");String[] cab={"Código","Concepto","Naturaleza","Importe de referencia","Moneda","Frecuencia","Aplicación","Periodo único","Inicio","Fin","Estado","Motivo","Modelo"};var h=sheet.createRow(0);for(int i=0;i<cab.length;i++){h.createCell(i).setCellValue(cab[i]);sheet.setColumnWidth(i,28*256);}int fila=1,pagina=0;org.springframework.data.domain.Page<NominaEmpleadoService.Fila> bloque;
   do{bloque=service.listar(empleadoId,filtro.pagina(pagina++,100));for(var a:bloque){var row=sheet.createRow(fila++);Object[] valores={a.codigo(),a.nombre(),a.tipo(),a.importe(),a.moneda(),a.frecuenciaTexto(),"UNICA".equals(a.modalidad())?"Sólo el periodo seleccionado":"LEGADO".equals(a.modelo())?"Regla anterior por nómina":"Recurrente",a.periodo(),fecha(a.inicio()),fecha(a.fin()),a.estado(),a.motivo(),a.modelo()};for(int i=0;i<valores.length;i++){var cell=row.createCell(i);if(valores[i] instanceof Number n)cell.setCellValue(n.doubleValue());else cell.setCellValue(Objects.toString(valores[i],""));}}}while(bloque.hasNext());sheet.createFreezePane(0,1);sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,cab.length-1));book.write(response.getOutputStream());
  }finally{book.dispose();}}
 }
 private static String fecha(LocalDate f){return f==null?"Sin fecha de fin":f.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
}
