package escuela.personal.controller;
import escuela.personal.service.*;
import escuela.personal.dto.*;
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
import java.util.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/configuracion-nomina")
public class ConfiguracionNominaController {
 private final ConfiguracionNominaService service;
 private final ConceptoEmpleadoService conceptos;
 private final EmpleadoService empleados;
 private FiltroConfiguracionNomina base(FiltroConfiguracionNomina original,Model m){var a=empleados.autorizarSalarios(false);var f=original.normalizado(service.hoy());m.addAttribute("filtro",f);m.addAttribute("volverUrl",f.url("/admin/configuracion-nomina"));m.addAttribute("puedeEditar",empleados.permitido("CONDICION_SALARIAL_ADMINISTRAR"));m.addAttribute("moduloActual",ModuloCatalogo.CONFIGURACION_NOMINA);var p=a.authorities().stream().map(x->x.getAuthority()).collect(java.util.stream.Collectors.toSet());m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("empleadoFiltro",f.empleadoId()==null?f.empleadoTexto():empleados.obtener(f.empleadoId()).codigo()+" · "+empleados.obtener(f.empleadoId()).nombre());m.addAttribute("calendarioFiltro",f.filtroCalendarioId()==null?f.filtroCalendarioTexto():service.calendario(f.filtroCalendarioId(),false));return f;}
 @GetMapping String listado(@ModelAttribute FiltroConfiguracionNomina filtro,Model m){var f=base(filtro,m);try{m.addAttribute("resultado",service.listar(f));}catch(ReglaNegocioException e){m.addAttribute("errorOperacion",e.getMessage());m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}return "admin/configuracion-nomina";}
 @GetMapping("/{id:\\d+}") String detalle(@PathVariable Long id,@ModelAttribute FiltroConfiguracionNomina filtro,@RequestParam(defaultValue="0")int paginaConceptos,@RequestParam(defaultValue="0")int paginaHistorial,Model m){var f=base(filtro,m);var e=service.obtener(id,f.fecha());m.addAttribute("empleado",e);m.addAttribute("conceptos",conceptos.vigentesEnFecha(id,f.fecha(),paginaConceptos));m.addAttribute("historial",service.historial(id,paginaHistorial));return "admin/configuracion-nomina-detalle";}
 @GetMapping("/{id:\\d+}/calendario") String editar(@PathVariable Long id,@ModelAttribute FiltroConfiguracionNomina filtro,Model m){empleados.autorizarSalarios(true);var f=base(filtro,m);var e=service.obtener(id,f.fecha());var form=new CalendarioEmpleadoForm();form.setCalendarioId(e.calendarioId());form.setCalendarioTexto(e.calendario());form.setVersion(e.version());m.addAttribute("empleado",e);m.addAttribute("form",form);return "admin/configuracion-nomina-form";}
 @PostMapping("/{id:\\d+}/calendario") String guardar(@PathVariable Long id,@Valid @ModelAttribute("form")CalendarioEmpleadoForm form,BindingResult errores,@ModelAttribute FiltroConfiguracionNomina filtro,Model m,RedirectAttributes flash){empleados.autorizarSalarios(true);if(!errores.hasErrors())try{service.guardarCalendario(id,form);flash.addFlashAttribute("mensaje","Calendario asignado con historial. No se cambiaron sueldos, conceptos, borradores ni saldos.");return "redirect:"+filtro.normalizado(service.hoy()).url("/admin/configuracion-nomina/"+id);}catch(ReglaNegocioException|org.springframework.dao.DataIntegrityViolationException e){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(e));}var f=base(filtro,m);m.addAttribute("empleado",service.obtener(id,f.fecha()));return "admin/configuracion-nomina-form";}
 @GetMapping("/calendarios/buscar") @ResponseBody ResponseEntity<ResultadoAutocompletado> calendarios(@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="false")boolean activos){var filas=service.calendarios(q,activos);return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(new ResultadoAutocompletado(filas.stream().limit(10).toList(),filas.size()>10));}
 @GetMapping("/excel") void excel(@ModelAttribute FiltroConfiguracionNomina original,HttpServletResponse r)throws IOException {var f=service.filtro(original);service.listar(f);r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=configuracion-nomina.xlsx");r.setHeader("Cache-Control","no-store");try(var b=new org.apache.poi.xssf.streaming.SXSSFWorkbook(100)){try{var s=b.createSheet("Configuración");String[] cab={"Código","Empleado","Expediente","Situación laboral","Fecha salarial consultada","Puesto histórico","Jornada histórica","Horas semanales","Sueldo bruto","Moneda","Base","Frecuencia salarial","Calendario actual","Frecuencia calendario","Calendario activo","Percepciones fijas","Deducciones fijas","Aportaciones escuela"};s.createRow(0);var h=s.getRow(0);for(int n=0;n<cab.length;n++){h.createCell(n).setCellValue(cab[n]);s.setColumnWidth(n,28*256);}int row=1,p=0;boolean mas;do{var page=service.listar(f.pagina(p++));mas=page.hasNext();for(var e:page){Object[] v={e.codigo(),e.nombre(),e.activo()?"Activo":"Inactivo",e.situacion(),f.fecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),e.puesto(),e.jornada(),e.horas(),e.sueldo(),e.moneda(),e.base(),e.frecuencia(),e.calendario(),e.frecuenciaCalendario(),e.calendarioActivo()?"Activo":"Sin calendario o inactivo",e.percepciones(),e.deducciones(),e.aportaciones()};var fila=s.createRow(row++);for(int n=0;n<v.length;n++){var c=fila.createCell(n);if(v[n] instanceof Number num)c.setCellValue(num.doubleValue());else c.setCellValue(Objects.toString(v[n],""));}}}while(mas);s.createFreezePane(0,1);b.write(r.getOutputStream());}finally{b.dispose();}}}
}
