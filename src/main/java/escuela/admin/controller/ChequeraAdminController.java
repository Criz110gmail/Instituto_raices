package escuela.admin.controller;

import escuela.finanzas.dto.*;
import escuela.finanzas.service.ChequeraService;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.*;
import java.io.IOException;

@Controller @RequiredArgsConstructor
@RequestMapping("/admin/cuentas-financieras/{cuentaId}/chequeras")
public class ChequeraAdminController {
 private final ChequeraService service;
 private void base(Long cuentaId,FiltroChequera filtro,Model m){m.addAttribute("cuenta",service.cuenta(cuentaId));m.addAttribute("filtro",filtro.normalizado());m.addAttribute("ruta","/admin/cuentas-financieras/"+cuentaId+"/chequeras");m.addAttribute("puedeEditar",service.puedeEditar());var a=org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();m.addAttribute("puedeEditarCuenta",a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("CUENTA_FINANCIERA_ADMINISTRAR")));}
 private void editar(){if(!service.puedeEditar())throw new AccessDeniedException("Se requiere Chequeras - Administrar");}
 @GetMapping String listado(@PathVariable Long cuentaId,@ModelAttribute FiltroChequera filtro,Model m){base(cuentaId,filtro,m);try{m.addAttribute("resultado",service.listar(cuentaId,filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}return "admin/chequeras";}
 @GetMapping("/nuevo") String nuevo(@PathVariable Long cuentaId,@ModelAttribute FiltroChequera filtro,Model m){editar();base(cuentaId,filtro,m);m.addAttribute("form",new ChequeraForm());return "admin/chequera-form";}
 @GetMapping("/{id}/editar") String formulario(@PathVariable Long cuentaId,@PathVariable Long id,@ModelAttribute FiltroChequera filtro,Model m){editar();base(cuentaId,filtro,m);var c=service.obtener(cuentaId,id);var f=new ChequeraForm();f.setNombre(c.nombre());f.setFolioInicial(c.folioInicial());f.setFolioFinal(c.folioFinal());f.setActivo(c.activo());f.setVersion(c.version());m.addAttribute("form",f);m.addAttribute("registroId",id);return "admin/chequera-form";}
 @PostMapping String guardar(@PathVariable Long cuentaId,@RequestParam(required=false)Long id,@Valid @ModelAttribute("form")ChequeraForm f,BindingResult errores,@ModelAttribute FiltroChequera filtro,Model m,RedirectAttributes flash){
  editar();base(cuentaId,filtro,m);if(id!=null)service.obtener(cuentaId,id);
  if(!errores.hasErrors())try{service.guardar(cuentaId,id,f);flash.addFlashAttribute("mensaje","Chequera guardada. No se emitieron cheques ni se movió dinero.");return "redirect:"+filtro.url("/admin/cuentas-financieras/"+cuentaId+"/chequeras");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}
  if(errores.hasErrors())m.addAttribute("errorOperacion","Completa los campos obligatorios y revisa los valores señalados en rojo.");m.addAttribute("registroId",id);return "admin/chequera-form";
 }
 @GetMapping("/{id}/historial") String historial(@PathVariable Long cuentaId,@PathVariable Long id,@RequestParam(defaultValue="0")int paginaHistorial,@ModelAttribute FiltroChequera filtro,Model m){base(cuentaId,filtro,m);m.addAttribute("chequera",service.obtener(cuentaId,id));m.addAttribute("historial",service.historial(cuentaId,id,paginaHistorial));return "admin/chequera-historial";}
 @GetMapping("/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> buscar(@PathVariable Long cuentaId,@RequestParam(defaultValue="")String q){var r=service.buscar(cuentaId,q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(r.stream().limit(10).toList(),r.size()>10));}
 @GetMapping("/excel") void excel(@PathVariable Long cuentaId,@ModelAttribute FiltroChequera filtro,HttpServletResponse response)throws IOException{
  var f=filtro.normalizado();service.listar(cuentaId,f.pagina(0));response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=chequeras.xlsx");response.setHeader("Cache-Control","no-store");
  try(var libro=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var hoja=libro.createSheet("Chequeras");String[] cab={"Chequera","Folio inicial","Folio final","Cantidad de folios","Estado","Último motivo"};var h=hoja.createRow(0);for(int i=0;i<cab.length;i++){h.createCell(i).setCellValue(cab[i]);hoja.setColumnWidth(i,(i==5?65:28)*256);}int fila=1,pagina=0;org.springframework.data.domain.Page<ChequeraService.Chequera> bloque;
   do{bloque=service.listar(cuentaId,f.pagina(pagina++));for(var c:bloque){var row=hoja.createRow(fila++);row.createCell(0).setCellValue(c.nombre());row.createCell(1).setCellValue(c.folioInicial());row.createCell(2).setCellValue(c.folioFinal());row.createCell(3).setCellValue(c.cantidad());row.createCell(4).setCellValue(c.activo()?"Activa":"Inactiva");row.createCell(5).setCellValue(c.motivo());}}while(bloque.hasNext());hoja.createFreezePane(0,1);hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,5));libro.write(response.getOutputStream());}finally{libro.dispose();}}
 }
}
