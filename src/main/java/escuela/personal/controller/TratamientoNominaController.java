package escuela.personal.controller;

import escuela.personal.dto.FiltroNomina;
import escuela.personal.service.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.admin.support.MensajeErrorFormulario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/preparacion-nomina/{idNomina}")
public class TratamientoNominaController {
 private final TratamientoNominaService service;
 private final PreparacionNominaService preparacion;
 private final EmpleadoService empleados;
 private final PagoEmpleadoNominaService pagos;
 private String base(Long id){return "/admin/preparacion-nomina/"+id;}
 private String pantalla(Long id,Long fila,FiltroNomina filtro,int pagina,Model m){
  var b=preparacion.obtener(id);m.addAttribute("borrador",b);m.addAttribute("empleado",preparacion.empleadoGuardado(id,fila));
  m.addAttribute("filtro",filtro.normalizado());m.addAttribute("lineasTratamiento",service.lineas(id,fila));m.addAttribute("tratamientoResumen",service.resumen(id,fila));m.addAttribute("eventos",service.historial(id,fila,pagina));
  m.addAttribute("puedeDecidir",b.estado().equals("BORRADOR")&&empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));m.addAttribute("rutaTratamiento",base(id)+"/empleados/"+fila+"/tratamientos");
  m.addAttribute("volverUrl",filtro.url(base(id)+"/empleados/"+fila));return "admin/nomina-tratamientos";
 }
 @GetMapping("/empleados/{fila}/tratamientos") String listado(@PathVariable("idNomina")Long id,@PathVariable Long fila,@RequestParam(defaultValue="0")int paginaEventos,@ModelAttribute FiltroNomina filtro,Model m){return pantalla(id,fila,filtro,paginaEventos,m);}
 @GetMapping("/empleados/{fila}/tratamientos/editar") String editar(@PathVariable("idNomina")Long id,@PathVariable Long fila,@RequestParam(defaultValue="EMPLEADO")String clave,@ModelAttribute FiltroNomina filtro,Model m){empleados.autorizarSalarios(true);m.addAttribute("claveCapturada",clave);m.addAttribute("accionCapturada","APLICAR");m.addAttribute("versionCapturada",preparacion.obtener(id).version());pantalla(id,fila,filtro,0,m);m.addAttribute("capturando",true);return "admin/nomina-tratamiento-form";}
 @PostMapping("/empleados/{fila}/tratamientos/revisar") String revisar(@PathVariable("idNomina")Long id,@PathVariable Long fila,@RequestParam Long version,@RequestParam String clave,@RequestParam String accion,@RequestParam String motivo,@ModelAttribute FiltroNomina filtro,Model m){
  empleados.autorizarSalarios(true);m.addAttribute("claveCapturada",clave);m.addAttribute("accionCapturada",accion);m.addAttribute("motivoCapturado",motivo);m.addAttribute("versionCapturada",version);
  try{if(motivo.isBlank()||motivo.length()>2000)throw new ReglaNegocioException("Explica el motivo obligatorio con máximo 2000 caracteres.");m.addAttribute("netoProyectado",service.proyectar(id,fila,clave,accion));m.addAttribute("selloDecision",PreparacionNominaService.sello(version+"|"+clave+"|"+accion+"|"+motivo));m.addAttribute("revisado",true);}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());}
  pantalla(id,fila,filtro,0,m);return "admin/nomina-tratamiento-form";
 }
 @PostMapping("/empleados/{fila}/tratamientos/confirmar") String confirmar(@PathVariable("idNomina")Long id,@PathVariable Long fila,@RequestParam Long version,@RequestParam String clave,@RequestParam String accion,@RequestParam String motivo,@RequestParam(defaultValue="false")boolean confirmado,@RequestParam(defaultValue="")String selloDecision,@ModelAttribute FiltroNomina filtro,Model m,RedirectAttributes flash){
  try{if(!PreparacionNominaService.sello(version+"|"+clave+"|"+accion+"|"+motivo).equals(selloDecision))throw new ReglaNegocioException("La captura cambió después de revisarla. Pulsa Revisar decisión nuevamente.");service.decidir(id,fila,version,clave,accion,motivo,confirmado);flash.addFlashAttribute("mensaje","Tratamiento guardado con auditoría. No cambió la configuración del empleado ni se movió dinero.");return "redirect:"+filtro.url(base(id)+"/empleados/"+fila+"/tratamientos");}
  catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));m.addAttribute("claveCapturada",clave);m.addAttribute("accionCapturada",accion);m.addAttribute("motivoCapturado",motivo);m.addAttribute("versionCapturada",version);pantalla(id,fila,filtro,0,m);return "admin/nomina-tratamiento-form";}
 }
 @GetMapping("/pendientes") String pendientes(@PathVariable("idNomina")Long id,@RequestParam(defaultValue="")String buscarPendiente,@RequestParam(defaultValue="TODOS")String estadoPendiente,@RequestParam(defaultValue="0")int paginaPendiente,@ModelAttribute FiltroNomina filtro,Model m){
  m.addAttribute("borrador",preparacion.obtener(id));m.addAttribute("filtro",filtro.normalizado());m.addAttribute("resultado",service.pendientes(id,buscarPendiente,estadoPendiente,paginaPendiente));m.addAttribute("buscarPendiente",buscarPendiente);m.addAttribute("estadoPendiente",estadoPendiente);m.addAttribute("rutaPendientes",base(id)+"/pendientes");m.addAttribute("puedePagar",pagos.puedePagar());return "admin/nomina-pendientes";
 }
 @PostMapping("/pendientes/{pendiente}/preparar") String preparar(@PathVariable("idNomina")Long id,@PathVariable Long pendiente,@RequestParam(defaultValue="false")boolean confirmado,@ModelAttribute FiltroNomina filtro,RedirectAttributes flash){
  if(!pagos.puedePagar())throw new org.springframework.security.access.AccessDeniedException("Falta autorización financiera");
  try{Long hijo=service.prepararPendiente(id,pendiente,confirmado);var fila=pagos.listar(hijo,"","TODOS",0,25).getContent().getFirst();return "redirect:"+filtro.url(base(hijo)+"/pago-multiple")+"&filaId="+fila.id();}
  catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){flash.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(e));return "redirect:"+filtro.url(base(id)+"/pendientes");}
 }
 @GetMapping("/pendientes/excel") void excel(@PathVariable("idNomina")Long id,@RequestParam(defaultValue="")String buscarPendiente,@RequestParam(defaultValue="TODOS")String estadoPendiente,HttpServletResponse response)throws IOException{
  service.pendientes(id,buscarPendiente,estadoPendiente,0);response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=pendientes-nomina.xlsx");response.setHeader("Cache-Control","no-store");
  try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var hoja=libro.createSheet("Pendientes");String[] titulos={"Empleado","Código","Periodo original","Importe firmado","Moneda","Estado","Tipo"};var cab=hoja.createRow(0);for(int i=0;i<titulos.length;i++){cab.createCell(i).setCellValue(titulos[i]);hoja.setColumnWidth(i,30*256);}int row=1,pag=0;org.springframework.data.domain.Page<TratamientoNominaService.Pendiente> page;do{page=service.pendientes(id,buscarPendiente,estadoPendiente,pag++);for(var e:page){var r=hoja.createRow(row++);r.createCell(0).setCellValue(e.nombre());r.createCell(1).setCellValue(e.codigo());r.createCell(2).setCellValue(e.periodo());r.createCell(3).setCellValue(e.importe().doubleValue());r.createCell(4).setCellValue(e.moneda());r.createCell(5).setCellValue(e.estado());r.createCell(6).setCellValue(e.importe().signum()>0?"Pago al empleado":"Recuperación / liquidación especializada");}}while(page.hasNext());hoja.createFreezePane(0,1);libro.write(response.getOutputStream());}finally{libro.dispose();}}
 }
 @GetMapping("/empleados/{fila}/tratamientos/excel") void tratamientosExcel(@PathVariable("idNomina")Long id,@PathVariable Long fila,HttpServletResponse response)throws IOException{
  var lineas=service.lineas(id,fila);response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=tratamientos-nomina.xlsx");response.setHeader("Cache-Control","no-store");try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var hoja=libro.createSheet("Conceptos");String[] titulos={"Código","Concepto","Tipo","Importe original","Tratamiento","Motivo"};var cab=hoja.createRow(0);for(int i=0;i<titulos.length;i++){cab.createCell(i).setCellValue(titulos[i]);hoja.setColumnWidth(i,30*256);}int n=1;for(var l:lineas){var r=hoja.createRow(n++);r.createCell(0).setCellValue(l.codigo());r.createCell(1).setCellValue(l.nombre());r.createCell(2).setCellValue(l.naturaleza());r.createCell(3).setCellValue(l.importe().doubleValue());r.createCell(4).setCellValue(l.accion());r.createCell(5).setCellValue(l.motivo());}var historia=libro.createSheet("Auditoría");int filaHistoria=0,pagina=0;org.springframework.data.domain.Page<TratamientoNominaService.Evento> page;do{page=service.historial(id,fila,pagina++);for(var e:page){var r=historia.createRow(filaHistoria++);r.createCell(0).setCellValue(e.fecha());r.createCell(1).setCellValue(e.usuario());r.createCell(2).setCellValue(e.clave());r.createCell(3).setCellValue(e.accion());r.createCell(4).setCellValue(e.motivo());}}while(page.hasNext());libro.write(response.getOutputStream());}finally{libro.dispose();}}
 }
}
