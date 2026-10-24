package escuela.personal.controller;
import escuela.personal.dto.*;
import escuela.personal.service.*;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;
import java.io.IOException;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/configuracion-nomina/{idEmpleado}/ocasionales")
public class ConceptoPeriodoNominaController {
 private final ConceptoPeriodoNominaService service;
 private final EmpleadoService empleados;
 private final ProgramacionNominaService periodos;
 private String ruta(Long emp){return "/admin/configuracion-nomina/"+emp+"/ocasionales";}
 private void base(Long emp,FiltroConfiguracionNomina origen,FiltroConceptoPeriodoNomina filtro,Model m){
  empleados.autorizarSalarios(false);var f=origen.normalizado(empleados.hoyLaboral(emp));var oc=filtro.normalizado();m.addAttribute("filtro",f);m.addAttribute("ocasFiltro",oc);m.addAttribute("empleado",empleados.obtener(emp));m.addAttribute("rutaOcasionales",ruta(emp));m.addAttribute("volverUrl",oc.url(f,ruta(emp)));m.addAttribute("configuracionUrl",f.url("/admin/configuracion-nomina/"+emp));m.addAttribute("puedeEditar",empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));
  m.addAttribute("periodoFiltroTexto",oc.periodoOcasionalId()==null?oc.periodoOcasionalTexto():periodos.periodo(oc.periodoOcasionalId()).codigo()+" · "+periodos.periodo(oc.periodoOcasionalId()).nombre());
 }
 @GetMapping String listado(@PathVariable("idEmpleado") Long empleadoId,@ModelAttribute FiltroConfiguracionNomina origen,@ModelAttribute FiltroConceptoPeriodoNomina filtro,Model m){base(empleadoId,origen,filtro,m);try{m.addAttribute("resultado",service.listar(empleadoId,filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}return "admin/conceptos-periodo-nomina";}
 @GetMapping("/nuevo") String nuevo(@PathVariable("idEmpleado") Long empleadoId,@ModelAttribute FiltroConfiguracionNomina origen,@ModelAttribute FiltroConceptoPeriodoNomina filtro,Model m){empleados.autorizarSalarios(true);base(empleadoId,origen,filtro,m);m.addAttribute("form",new ConceptoPeriodoNominaForm());return "admin/concepto-periodo-nomina-form";}
 @PostMapping String guardar(@PathVariable("idEmpleado") Long empleadoId,@Valid @ModelAttribute("form")ConceptoPeriodoNominaForm form,BindingResult errores,@ModelAttribute FiltroConfiguracionNomina origen,@ModelAttribute FiltroConceptoPeriodoNomina filtro,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);empleados.autorizar(empleadoId,true,false);if(!errores.hasErrors())try{Long id=service.guardar(empleadoId,form);flash.addFlashAttribute("mensaje","Concepto registrado sólo para el periodo elegido. No se pagó ni consumió y no cambió una nómina guardada.");return "redirect:"+filtro.url(origen.normalizado(empleados.hoyLaboral(empleadoId)),ruta(empleadoId)+"/"+id);}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  if(errores.hasErrors())m.addAttribute("errorOperacion","Revisa los campos y espera la consulta de periodo y concepto. No se guardó ningún cambio.");base(empleadoId,origen,filtro,m);return "admin/concepto-periodo-nomina-form";
 }
 @GetMapping("/{id:\\d+}") String detalle(@PathVariable("idEmpleado") Long empleadoId,@PathVariable Long id,@RequestParam(defaultValue="0")int paginaHistoriaOcasional,@ModelAttribute FiltroConfiguracionNomina origen,@ModelAttribute FiltroConceptoPeriodoNomina filtro,Model m){base(empleadoId,origen,filtro,m);m.addAttribute("asignacion",service.obtener(empleadoId,id));m.addAttribute("historial",service.historial(empleadoId,id,paginaHistoriaOcasional));return "admin/concepto-periodo-nomina-detalle";}
 @PostMapping("/{id:\\d+}/cancelar") String cancelar(@PathVariable("idEmpleado") Long empleadoId,@PathVariable Long id,@RequestParam Long version,@RequestParam String motivo,@ModelAttribute FiltroConfiguracionNomina origen,@ModelAttribute FiltroConceptoPeriodoNomina filtro,Model m){empleados.autorizarSalarios(true);try{service.cancelar(empleadoId,id,version,motivo);m.addAttribute("mensaje","Concepto ocasional cancelado con historial. No se cambió una nómina aplicada ni se movió dinero; actualiza los borradores que lo incluían.");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));m.addAttribute("motivoCapturado",motivo);}return detalle(empleadoId,id,0,origen,filtro,m);}
 @GetMapping("/contexto") @ResponseBody ResponseEntity<?> contexto(@PathVariable("idEmpleado") Long empleadoId,@RequestParam Long conceptoId,@RequestParam Long periodoId){try{return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.contexto(empleadoId,conceptoId,periodoId));}catch(ReglaNegocioException e){return ResponseEntity.badRequest().cacheControl(CacheControl.noStore()).body(Map.of("mensaje",e.getMessage()));}}
 @GetMapping("/conceptos/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> conceptos(@PathVariable("idEmpleado") Long empleadoId,@RequestParam(defaultValue="")String q){var r=service.conceptos(empleadoId,q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
 @GetMapping("/periodos/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> periodos(@PathVariable("idEmpleado") Long empleadoId,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="false")boolean todos){var r=service.periodos(empleadoId,q,todos);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
 @GetMapping("/excel") void excel(@PathVariable("idEmpleado") Long empleadoId,@ModelAttribute FiltroConceptoPeriodoNomina original,HttpServletResponse response)throws IOException{
  service.listar(empleadoId,original);response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=conceptos-por-periodo.xlsx");response.setHeader("Cache-Control","no-store");
  try(var b=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var s=b.createSheet("Conceptos por periodo");String[] cab={"Concepto","Tipo","Periodo","Importe","Moneda","Inicio","Fin","Estado","Nómina aplicada","Motivo","Última gestión"};var h=s.createRow(0);for(int n=0;n<cab.length;n++){h.createCell(n).setCellValue(cab[n]);s.setColumnWidth(n,30*256);}var dinero=b.createCellStyle();dinero.setDataFormat(b.createDataFormat().getFormat("\"$\"#,##0.00"));var fecha=java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");int row=1,pagina=0;boolean mas;do{var page=service.listar(empleadoId,original.pagina(pagina++));mas=page.hasNext();for(var e:page){Object[] datos={e.concepto(),e.tipo(),e.periodo(),e.importe(),e.moneda(),e.inicio().format(fecha),e.fin().format(fecha),e.estado(),e.aplicada()?"Sí":"No",e.motivo(),e.motivoOperacion()};var r=s.createRow(row++);for(int n=0;n<datos.length;n++){var c=r.createCell(n);if(datos[n] instanceof Number num){c.setCellValue(num.doubleValue());c.setCellStyle(dinero);}else c.setCellValue(Objects.toString(datos[n],""));}}}while(mas);s.createFreezePane(0,1);b.write(response.getOutputStream());}finally{b.dispose();}}
 }
}
