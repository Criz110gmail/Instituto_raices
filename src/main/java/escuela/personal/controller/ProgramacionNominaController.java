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
import java.time.LocalDate;
import java.util.*;

@Controller @RequiredArgsConstructor
public class ProgramacionNominaController {
 private final ProgramacionNominaService service;
 private final EmpleadoService empleados;
 private static boolean periodos(String tipo){return tipo.equals("periodos-nomina");}
 private void base(String tipo,FiltroProgramacionNomina original,Model m){
  var p=empleados.autorizarSalarios(false);boolean per=periodos(tipo);var f=original.normalizado(per);var modulo=per?ModuloCatalogo.PERIODOS_NOMINA:ModuloCatalogo.CALENDARIOS_NOMINA;
  m.addAttribute("filtro",f);m.addAttribute("tipo",tipo);m.addAttribute("esPeriodo",per);m.addAttribute("titulo",modulo.titulo());m.addAttribute("volverUrl",f.url("/admin/"+tipo));m.addAttribute("puedeEditar",empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));
  m.addAttribute("calendarioFiltro",f.filtroCalendarioTexto());if(f.filtroCalendarioId()!=null){var c=service.calendario(f.filtroCalendarioId());m.addAttribute("calendarioFiltro",c.codigo()+" · "+c.nombre());}
  m.addAttribute("periodoFiltro",f.filtroPeriodoTexto());if(f.filtroPeriodoId()!=null){var a=service.periodo(f.filtroPeriodoId());m.addAttribute("periodoFiltro",a.codigo()+" · "+a.nombre());}
  var permisos=p.authorities().stream().map(x->x.getAuthority()).collect(java.util.stream.Collectors.toSet());m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(permisos)).toList());m.addAttribute("moduloActual",modulo);
 }
 @GetMapping("/admin/{tipo:calendarios-nomina|periodos-nomina}") String listado(@PathVariable String tipo,@ModelAttribute FiltroProgramacionNomina filtro,Model m){base(tipo,filtro,m);try{m.addAttribute("resultado",periodos(tipo)?service.periodos(filtro):service.calendarios(filtro));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}return "admin/programacion-nomina-listado";}
 @GetMapping("/admin/{tipo:calendarios-nomina|periodos-nomina}/nuevo") String nuevo(@PathVariable String tipo,@ModelAttribute FiltroProgramacionNomina filtro,Model m){empleados.autorizarSalarios(true);base(tipo,filtro,m);m.addAttribute("form",periodos(tipo)?new PeriodoNominaForm():new CalendarioNominaForm());m.addAttribute("frecuenciaFija",false);return periodos(tipo)?"admin/periodo-nomina-form":"admin/calendario-nomina-form";}
 @GetMapping("/admin/{tipo:calendarios-nomina|periodos-nomina}/{id:\\d+}/editar") String editar(@PathVariable String tipo,@PathVariable Long id,@ModelAttribute FiltroProgramacionNomina filtro,Model m){
  empleados.autorizarSalarios(true);base(tipo,filtro,m);m.addAttribute("registroId",id);
  if(periodos(tipo)){var a=service.periodo(id);if(a.estado().equals("CANCELADO")){m.addAttribute("errorOperacion","El periodo está cancelado; consulta su detalle y registra otro si corresponde.");return detalle(tipo,id,filtro,0,m);}var f=new PeriodoNominaForm();f.setCodigo(a.codigo());f.setNombre(a.nombre());f.setCalendarioId(a.calendarioId());f.setCalendarioTexto(a.calendarioTexto());f.setCalendarioVersion(a.calendarioVersion());f.setInicio(a.inicio());f.setFin(a.fin());f.setCorte(a.corte());f.setPago(a.pago());f.setVersion(a.version());m.addAttribute("form",f);m.addAttribute("frecuenciaPeriodo",a.frecuenciaTexto());return "admin/periodo-nomina-form";}
  var c=service.calendario(id);var f=new CalendarioNominaForm();f.setCodigo(c.codigo());f.setNombre(c.nombre());f.setDescripcion(c.descripcion());f.setFrecuencia(c.frecuencia());f.setActivo(c.activo());f.setVersion(c.version());m.addAttribute("form",f);m.addAttribute("frecuenciaFija",c.frecuenciaFija());m.addAttribute("calendarioActual",c);return "admin/calendario-nomina-form";
 }
 @PostMapping("/admin/calendarios-nomina") String guardarCalendario(@RequestParam(required=false)Long id,@Valid @ModelAttribute("form")CalendarioNominaForm form,BindingResult errores,@ModelAttribute FiltroProgramacionNomina filtro,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);if(id!=null)service.calendario(id);
  if(!errores.hasErrors())try{service.guardarCalendario(id,form);flash.addFlashAttribute("mensaje","Calendario guardado. No se generaron periodos, nóminas ni movimientos de dinero.");return "redirect:"+filtro.url("/admin/calendarios-nomina");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  if(errores.hasErrors())m.addAttribute("errorOperacion","Completa los campos obligatorios y revisa los formatos indicados.");base("calendarios-nomina",filtro,m);m.addAttribute("registroId",id);var c=id==null?null:service.calendario(id);m.addAttribute("frecuenciaFija",c!=null&&c.frecuenciaFija());m.addAttribute("calendarioActual",c);return "admin/calendario-nomina-form";
 }
 @PostMapping("/admin/periodos-nomina") String guardarPeriodo(@RequestParam(required=false)Long id,@Valid @ModelAttribute("form")PeriodoNominaForm form,BindingResult errores,@ModelAttribute FiltroProgramacionNomina filtro,Model m,RedirectAttributes flash){
  empleados.autorizarSalarios(true);if(id!=null)service.periodo(id);
  if(!errores.hasErrors())try{Long guardado=service.guardarPeriodo(id,form);flash.addFlashAttribute("mensaje","Periodo programado. No se seleccionaron empleados ni se calcularon sueldos, nóminas o pagos.");return "redirect:"+filtro.url("/admin/periodos-nomina/"+guardado);}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}
  if(errores.hasErrors())m.addAttribute("errorOperacion","Completa los campos marcados, selecciona el calendario y espera su consulta antes de confirmar.");base("periodos-nomina",filtro,m);m.addAttribute("registroId",id);if(id!=null)m.addAttribute("frecuenciaPeriodo",service.periodo(id).frecuenciaTexto());return "admin/periodo-nomina-form";
 }
 @GetMapping("/admin/{tipo:calendarios-nomina|periodos-nomina}/{id:\\d+}") String detalle(@PathVariable String tipo,@PathVariable Long id,@ModelAttribute FiltroProgramacionNomina filtro,@RequestParam(defaultValue="0")int paginaHistorial,Model m){base(tipo,filtro,m);m.addAttribute("registro",periodos(tipo)?service.periodo(id):service.calendario(id));m.addAttribute("historial",service.historial(periodos(tipo),id,paginaHistorial));return "admin/programacion-nomina-detalle";}
 @PostMapping("/admin/periodos-nomina/{id:\\d+}/cancelar") String cancelar(@PathVariable Long id,@RequestParam Long version,@RequestParam String motivo,@ModelAttribute FiltroProgramacionNomina filtro,Model m){
  empleados.autorizarSalarios(true);try{service.cancelar(id,version,motivo);m.addAttribute("mensaje","Periodo cancelado. Conserva su historial; no se cancelaron pagos ni se modificaron cuentas.");}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));m.addAttribute("motivoCapturado",motivo);}
  m.addAttribute("tabInicial","gestion");return detalle("periodos-nomina",id,filtro,0,m);
 }
 @GetMapping("/admin/{tipo:calendarios-nomina|periodos-nomina}/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> buscar(@PathVariable String tipo,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="false")boolean captura){var filas=service.buscar(periodos(tipo),q,captura);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/admin/calendarios-nomina/{id:\\d+}/contexto") @ResponseBody ResponseEntity<ProgramacionNominaService.Contexto> contexto(@PathVariable Long id){return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(service.contexto(id));}
 @GetMapping("/admin/{tipo:calendarios-nomina|periodos-nomina}/excel") void excel(@PathVariable String tipo,@ModelAttribute FiltroProgramacionNomina original,HttpServletResponse response)throws IOException{
  boolean per=periodos(tipo);var f=original.normalizado(per);if(per)service.periodos(f.pagina(0,100));else service.calendarios(f.pagina(0,100));
  response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename="+tipo+".xlsx");response.setHeader("Cache-Control","no-store");
  try(var book=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var sheet=book.createSheet(per?"Periodos de nómina":"Calendarios de nómina");String[] cab=per?new String[]{"Código","Nombre","Calendario histórico","Frecuencia histórica","Inicio","Fin","Días incluidos","Fecha límite para recibir incidencias de nómina (opcional)","Pago previsto","Estado","Situación por fechas","Motivo de registro","Última gestión"}:new String[]{"Código","Nombre","Frecuencia","Descripción","Estado","Frecuencia protegida por periodos"};var h=sheet.createRow(0);for(int i=0;i<cab.length;i++){h.createCell(i).setCellValue(cab[i]);sheet.setColumnWidth(i,30*256);}int fila=1,pagina=0;boolean mas;
   do{if(per){var page=service.periodos(f.pagina(pagina++,100));mas=page.hasNext();for(var a:page)escribir(sheet.createRow(fila++),new Object[]{a.codigo(),a.nombre(),a.calendarioTexto(),a.frecuenciaTexto(),fecha(a.inicio()),fecha(a.fin()),a.dias(),fecha(a.corte()),fecha(a.pago()),a.estadoTexto(),a.situacion(),a.motivoRegistro(),a.motivoOperacion()});}else{var page=service.calendarios(f.pagina(pagina++,100));mas=page.hasNext();for(var c:page)escribir(sheet.createRow(fila++),new Object[]{c.codigo(),c.nombre(),c.frecuenciaTexto(),c.descripcion(),c.activo()?"Activo":"Inactivo",c.frecuenciaFija()?"Sí":"No"});}}while(mas);
   sheet.createFreezePane(0,1);sheet.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,cab.length-1));book.write(response.getOutputStream());
  }finally{book.dispose();}}
 }
 private static void escribir(org.apache.poi.ss.usermodel.Row row,Object[] valores){for(int i=0;i<valores.length;i++){var cell=row.createCell(i);if(valores[i] instanceof Number n)cell.setCellValue(n.doubleValue());else cell.setCellValue(Objects.toString(valores[i],""));}}
 private static String fecha(LocalDate f){return f==null?"Sin definir":f.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));}
}
