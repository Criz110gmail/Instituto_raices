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
import org.springframework.http.*;
import java.io.IOException;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/conceptos-nomina")
public class ConceptoNominaController {
 private final ConceptoNominaService service;
 private final EmpleadoService empleados;
 private void base(FiltroConceptoNomina original,Model m){
  var p=empleados.autorizarSalarios(false);var f=original.normalizado();m.addAttribute("filtro",f);m.addAttribute("volverUrl",f.url("/admin/conceptos-nomina"));m.addAttribute("puedeEditar",empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));
  m.addAttribute("conceptoFiltro",f.conceptoTexto());if(f.conceptoId()!=null){var c=service.obtener(f.conceptoId());m.addAttribute("conceptoFiltro",c.codigo()+" · "+c.nombre());}
  var permisos=p.authorities().stream().map(x->x.getAuthority()).collect(java.util.stream.Collectors.toSet());m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(permisos)).toList());m.addAttribute("moduloActual",ModuloCatalogo.CONCEPTOS_NOMINA);
 }
 @GetMapping String listado(@ModelAttribute FiltroConceptoNomina filtro,Model m){base(filtro,m);try{m.addAttribute("resultado",service.listar(filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}return "admin/conceptos-nomina";}
 @GetMapping("/nuevo") String nuevo(@ModelAttribute FiltroConceptoNomina filtro,Model m){empleados.autorizarSalarios(true);base(filtro,m);m.addAttribute("form",new ConceptoNominaForm());return "admin/concepto-nomina-form";}
 @GetMapping("/{id:\\d+}/editar") String editar(@PathVariable Long id,@ModelAttribute FiltroConceptoNomina filtro,Model m){
  empleados.autorizarSalarios(true);var c=service.obtener(id);var form=new ConceptoNominaForm();form.setCodigo(c.codigo());form.setNombre(c.nombre());form.setDescripcion(c.descripcion());form.setNaturaleza(c.naturaleza());form.setAplicacion(c.aplicacion());form.setActivo(c.activo());form.setVersion(c.version());base(filtro,m);m.addAttribute("form",form);m.addAttribute("registroId",id);return "admin/concepto-nomina-form";
 }
 @PostMapping String guardar(@RequestParam(required=false)Long id,@Valid @ModelAttribute("form")ConceptoNominaForm form,BindingResult errores,@ModelAttribute FiltroConceptoNomina filtro,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);if(id!=null)service.obtener(id);
  if(!errores.hasErrors())try{service.guardar(id,form);flash.addFlashAttribute("mensaje","Concepto de nómina guardado. No se asignaron importes ni se generaron pagos o descuentos.");return "redirect:"+filtro.url("/admin/conceptos-nomina");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  if(errores.hasErrors())m.addAttribute("errorOperacion","Completa los campos obligatorios y revisa los formatos indicados.");base(filtro,m);m.addAttribute("registroId",id);return "admin/concepto-nomina-form";
 }
 @GetMapping("/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> buscar(@RequestParam(defaultValue="")String q){var filas=service.buscar(q);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/excel") void excel(@ModelAttribute FiltroConceptoNomina original,HttpServletResponse response)throws IOException{
  var f=original.normalizado();service.listar(f.pagina(0,100));
  response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=conceptos-nomina.xlsx");response.setHeader("Cache-Control","no-store");
  try(var book=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var sheet=book.createSheet("Conceptos de nómina");String[] cab={"Código","Nombre","Tipo de concepto","Efecto de referencia","Forma de aplicación","Descripción","Estado"};var h=sheet.createRow(0);for(int i=0;i<cab.length;i++){h.createCell(i).setCellValue(cab[i]);sheet.setColumnWidth(i,35*256);}int fila=1,pagina=0;org.springframework.data.domain.Page<ConceptoNominaService.Concepto> bloque;
   do{bloque=service.listar(f.pagina(pagina++,100));for(var c:bloque){var row=sheet.createRow(fila++);String[] valores={c.codigo(),c.nombre(),c.tipoTexto(),c.efecto(),c.aplicacionTexto(),c.descripcion(),c.activo()?"Activo":"Inactivo"};for(int i=0;i<valores.length;i++)row.createCell(i).setCellValue(Objects.toString(valores[i],""));}}while(bloque.hasNext());
   sheet.createFreezePane(0,1);sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,cab.length-1));book.write(response.getOutputStream());
  }finally{book.dispose();}}
 }
}
