package escuela.pendiente.controller;

import escuela.pendiente.dto.*;
import escuela.pendiente.entity.EstadoPendiente;
import escuela.pendiente.service.PendienteService;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.admin.support.MensajeErrorFormulario;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.*;
import org.springframework.data.domain.Page;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.IOException;

@Controller @RequiredArgsConstructor @RequestMapping({"/admin/pendientes","/maestros/pendientes"})
public class PendienteController {
 private final PendienteService service;
 @InitBinder("form") void campos(org.springframework.web.bind.WebDataBinder b){b.setAllowedFields("titulo","descripcion","fechaCompromiso","estado","version");}
 private boolean docente(HttpServletRequest r){return r.getRequestURI().substring(r.getContextPath().length()).startsWith("/maestros/");}
 private String base(HttpServletRequest r){return docente(r)?"/maestros/pendientes":"/admin/pendientes";}
 private void preparar(Model m,HttpServletRequest r){m.addAttribute("docente",docente(r));m.addAttribute("base",base(r));m.addAttribute("estados",EstadoPendiente.values());}
 @GetMapping String lista(@AuthenticationPrincipal UsuarioPrincipal p,@ModelAttribute("filtro") PendienteFiltro f,HttpServletRequest r,Model m){
  preparar(m,r);service.actor(p,docente(r));
  try{f=f.normalizado();f.validar();m.addAttribute("resultado",service.listar(p,docente(r),f));}
  catch(escuela.common.exception.ReglaNegocioException e){m.addAttribute("errorFiltro",e.getMessage());f=new PendienteFiltro(f.q(),"ABIERTOS",f.desde(),f.hasta(),0,25).normalizado();m.addAttribute("resultado",Page.empty());}
  m.addAttribute("filtro",f);m.addAttribute("avisos",service.avisos(p,docente(r)));return "pendientes/lista";
 }
 @GetMapping("/nuevo") String nuevo(@AuthenticationPrincipal UsuarioPrincipal p,HttpServletRequest r,Model m){preparar(m,r);var f=new PendienteForm();f.setFechaCompromiso(service.hoy(p,docente(r)));m.addAttribute("form",f);return "pendientes/formulario";}
 @GetMapping("/{id}/editar") String editar(@PathVariable Long id,@AuthenticationPrincipal UsuarioPrincipal p,HttpServletRequest r,Model m){
  preparar(m,r);var v=service.obtener(p,docente(r),id);m.addAttribute("form",v.formulario());m.addAttribute("id",id);return "pendientes/formulario";
 }
 @GetMapping("/{id}") String detalle(@PathVariable Long id,@RequestParam(defaultValue="0")int paginaHistorial,@AuthenticationPrincipal UsuarioPrincipal p,HttpServletRequest r,Model m){
  preparar(m,r);m.addAttribute("pendiente",service.obtener(p,docente(r),id));m.addAttribute("historial",service.historial(p,docente(r),id,paginaHistorial));return "pendientes/detalle";
 }
 @PostMapping({"","/{id}"}) String guardar(@PathVariable(required=false)Long id,@Valid @ModelAttribute("form")PendienteForm f,BindingResult errores,@AuthenticationPrincipal UsuarioPrincipal p,HttpServletRequest r,Model m,RedirectAttributes flash){
  service.actor(p,docente(r));if(id!=null)service.obtener(p,docente(r),id);preparar(m,r);m.addAttribute("id",id);
  if(!errores.hasErrors())try{Long guardado=service.guardar(p,docente(r),id,f);flash.addFlashAttribute("mensaje","Pendiente guardado. Sólo tú puedes consultarlo.");return "redirect:"+base(r)+"/"+guardado;}
  catch(escuela.common.exception.ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException|org.springframework.orm.ObjectOptimisticLockingFailureException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  return "pendientes/formulario";
 }
 @PostMapping("/{id}/estado") String estado(@PathVariable Long id,@RequestParam Long version,@RequestParam EstadoPendiente estado,@AuthenticationPrincipal UsuarioPrincipal p,HttpServletRequest r,Model m,RedirectAttributes flash){
  service.obtener(p,docente(r),id);
  try{service.estado(p,docente(r),id,version,estado);flash.addFlashAttribute("mensaje","Estado actualizado: "+estado.getEtiqueta());return "redirect:"+base(r)+"/"+id;}
  catch(escuela.common.exception.ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException|org.springframework.orm.ObjectOptimisticLockingFailureException e){preparar(m,r);m.addAttribute("pendiente",service.obtener(p,docente(r),id));m.addAttribute("historial",service.historial(p,docente(r),id,0));m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));return "pendientes/detalle";}
 }
 @GetMapping("/avisos") @ResponseBody ResponseEntity<PendienteService.Avisos> avisos(@AuthenticationPrincipal UsuarioPrincipal p,HttpServletRequest r){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.avisos(p,docente(r)));}
 @GetMapping("/excel") void excel(@AuthenticationPrincipal UsuarioPrincipal p,@ModelAttribute PendienteFiltro original,HttpServletRequest r,HttpServletResponse response)throws IOException{
  var f=original.normalizado();f.validar();var pagina=service.listar(p,docente(r),f.pagina(0,100));
  response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=mis-pendientes.xlsx");response.setHeader("Cache-Control","no-store");
  try(var l=new SXSSFWorkbook(200)){
   l.setCompressTempFiles(true);var h=l.createSheet("Mis pendientes");var cab=l.createCellStyle();cab.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());cab.setFillPattern(FillPatternType.SOLID_FOREGROUND);var fuente=l.createFont();fuente.setBold(true);fuente.setColor(IndexedColors.WHITE.getIndex());cab.setFont(fuente);
   String[] columnas={"Título","Descripción","Fecha compromiso","Estado","Vencido"};var encabezado=h.createRow(0);for(int c=0;c<columnas.length;c++){var celda=encabezado.createCell(c);celda.setCellValue(columnas[c]);celda.setCellStyle(cab);h.setColumnWidth(c,(c<2?48:22)*256);}
   var fecha=l.createCellStyle();fecha.setDataFormat(l.createDataFormat().getFormat("dd/mm/yyyy"));int fila=1,numero=0;
   while(true){for(var v:pagina){var row=h.createRow(fila++);row.createCell(0).setCellValue(v.titulo());row.createCell(1).setCellValue(v.descripcion()==null?"":v.descripcion());row.createCell(2).setCellValue(v.fechaCompromiso());row.getCell(2).setCellStyle(fecha);row.createCell(3).setCellValue(v.estado().getEtiqueta());row.createCell(4).setCellValue(v.vencido()?"Sí":"No");}if(!pagina.hasNext())break;pagina=service.listar(p,docente(r),f.pagina(++numero,100));}
   h.createFreezePane(0,1);l.write(response.getOutputStream());l.dispose();
  }
 }
}
