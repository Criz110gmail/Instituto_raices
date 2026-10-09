package escuela.admin.controller;
import escuela.cobranza.dto.AnticipoForm;
import escuela.cobranza.service.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.admin.dto.ModuloCatalogo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import jakarta.servlet.http.HttpServletResponse;
import java.time.*;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/convenios-pago/anticipados")
public class AnticipoAdminController {
 private final AcuerdoAnticipadoService service;
 private final ConvenioPagoService convenios;
 private final InstitucionService instituciones;
 private final AlcanceDatosService alcance;
 @GetMapping String listado(@RequestParam(required=false)Long institucionId,@RequestParam(defaultValue="")String estado,@RequestParam(defaultValue="")String texto,@RequestParam(defaultValue="0")int pagina,Model m,Authentication a){
  var ins=alcance.filtrarInstituciones(instituciones.listar());if(institucionId==null&&!ins.isEmpty())institucionId=ins.getFirst().id();m.addAttribute("instituciones",ins);m.addAttribute("institucionId",institucionId);m.addAttribute("estado",estado);m.addAttribute("texto",texto);m.addAttribute("resultado",institucionId==null?Page.empty():service.listar(institucionId,estado,texto,pagina,25));menu(m,a);return "admin/anticipos";
 }
 @GetMapping("/nuevo") String nuevo(Model m,Authentication a){var f=new AnticipoForm();var ins=alcance.filtrarInstituciones(instituciones.listar());if(ins.size()==1)f.setInstitucionId(ins.getFirst().id());f.setFechaLimite(LocalDate.now().plusDays(30));m.addAttribute("form",f);preparar(m,a,f);return "admin/anticipo-form";}
 @GetMapping("/cargos") @ResponseBody List<AcuerdoAnticipadoService.Fila> cargos(@RequestParam Long institucionId,@RequestParam Long tutorId,@RequestParam(defaultValue="")String q,@RequestParam(required=false)List<Long> excluir){return service.buscar(institucionId,tutorId,q,excluir);}
 @PostMapping("/previsualizar") String preview(@Valid @ModelAttribute("form")AnticipoForm f,BindingResult errores,Model m,Authentication a){if(!errores.hasErrors())try{var p=service.previsualizar(f);f.setHuella(p.huella());m.addAttribute("previa",p);}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}preparar(m,a,f);return "admin/anticipo-form";}
 @PostMapping String guardar(@Valid @ModelAttribute("form")AnticipoForm f,BindingResult errores,Model m,Authentication a,RedirectAttributes flash){if(!errores.hasErrors())try{Long id=service.crear(f);flash.addFlashAttribute("mensaje","Propuesta guardada. Los adeudos y becas no cambian hasta validar el pago completo.");return "redirect:/admin/convenios-pago/anticipados/"+id;}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}preparar(m,a,f);return "admin/anticipo-form";}
 @GetMapping("/{id}") String detalle(@PathVariable Long id,Model m,Authentication a){m.addAttribute("detalle",service.obtener(id));menu(m,a);return "admin/anticipo-detalle";}
 @PostMapping("/{id}/pago") String pago(@PathVariable Long id,@Valid @ModelAttribute AcuerdoAnticipadoService.PagoForm form,BindingResult errores,@RequestParam(required=false,name="comprobantes")List<MultipartFile> archivos,RedirectAttributes flash){if(errores.hasErrors()){flash.addFlashAttribute("errorOperacion","Selecciona método y cuenta, y captura una fecha/hora y referencia válidas.");return "redirect:/admin/convenios-pago/anticipados/"+id;}try{return "redirect:/admin/pagos/"+service.registrarPago(id,form,archivos)+"/editar";}catch(RuntimeException ex){flash.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return "redirect:/admin/convenios-pago/anticipados/"+id;}}
 @PostMapping("/{id}/cancelar") String cancelar(@PathVariable Long id,@RequestParam Long version,@RequestParam String motivo,RedirectAttributes flash){try{service.cancelar(id,version,motivo);flash.addFlashAttribute("mensaje","Propuesta cancelada; las becas y los adeudos anteriores no se modificaron.");}catch(RuntimeException ex){flash.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}return "redirect:/admin/convenios-pago/anticipados/"+id;}
 @GetMapping("/excel") void excel(@RequestParam Long institucionId,@RequestParam(defaultValue="")String estado,@RequestParam(defaultValue="")String texto,HttpServletResponse r)throws java.io.IOException {
  var bloque=service.listar(institucionId,estado,texto,0,100);
  try(var libro=new XSSFWorkbook()) {
   var hoja=libro.createSheet("Acuerdos anticipados");
   var dinero=libro.createCellStyle();dinero.setDataFormat(libro.createDataFormat().getFormat("#,##0.00;(#,##0.00)"));
   var cabecera=libro.createCellStyle();var fuente=libro.createFont();fuente.setBold(true);fuente.setColor(org.apache.poi.ss.usermodel.IndexedColors.WHITE.getIndex());cabecera.setFont(fuente);cabecera.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.DARK_TEAL.getIndex());cabecera.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
   String[] columnas={"Folio","Tutor","Plantel","Estado","Fecha límite","Política de beca","Beneficio","Alumno","Concepto","Periodo","Original","Beca previa","Otros ajustes previos","Base acordada","Beneficio adicional","Por pagar","Moneda","Motivo","Autorizado por"};
   var titulo=hoja.createRow(0);for(int i=0;i<columnas.length;i++){var cell=titulo.createCell(i);cell.setCellValue(columnas[i]);cell.setCellStyle(cabecera);}int fila=1;
   while(true) {
    for(var d:bloque)for(var c:d.filas()) {
     var row=hoja.createRow(fila++);String[] valores={d.folio(),d.tutor(),d.plantel(),d.estado(),d.limite().toString(),d.politicaBeca(),d.tipoBeneficio(),c.alumno(),c.concepto(),c.periodo(),c.original().toPlainString(),c.beca().toPlainString(),c.otrosAjustes().toPlainString(),c.base().toPlainString(),c.beneficio().toPlainString(),c.pagar().toPlainString(),c.moneda(),d.motivo(),d.autorizadoPor()};
     for(int i=0;i<valores.length;i++){var cell=row.createCell(i);if(i>=10&&i<=15){cell.setCellValue(Double.parseDouble(valores[i]));cell.setCellStyle(dinero);}else cell.setCellValue(valores[i]);}
    }
    if(!bloque.hasNext())break;bloque=service.listar(institucionId,estado,texto,bloque.getNumber()+1,100);
   }
   hoja.createFreezePane(0,1);hoja.setAutoFilter(new org.apache.poi.ss.util.CellRangeAddress(0,Math.max(0,fila-1),0,columnas.length-1));for(int i=0;i<columnas.length;i++)hoja.setColumnWidth(i,24*256);
   r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=acuerdos-anticipados.xlsx");libro.write(r.getOutputStream());
  }
 }
 private void preparar(Model m,Authentication a,AnticipoForm f){m.addAttribute("instituciones",alcance.filtrarInstituciones(instituciones.listar()));if(f.getTutorId()!=null)alcance.validarRecurso(ModuloCatalogo.TUTORES,f.getTutorId());m.addAttribute("tutorSeleccionado",convenios.tutorEtiqueta(f.getTutorId()));m.addAttribute("seleccionadas",service.seleccionadas(f.getCargoIds()));menu(m,a);}
 private void menu(Model m,Authentication a){Set<String> p=new HashSet<>();if(a!=null)a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.CONVENIOS_PAGO);m.addAttribute("puedeRegistrarPago",p.contains("PAGO_REGISTRAR"));}
}
