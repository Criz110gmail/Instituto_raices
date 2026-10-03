package escuela.admin.controller;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.dto.*;
import escuela.cobranza.entity.EstadoConvenioPago;
import escuela.cobranza.service.ConvenioPagoService;
import escuela.cobranza.service.ExcelConvenioPagoService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/convenios-pago")
public class ConvenioPagoAdminController {
 private final ConvenioPagoService service;private final ExcelConvenioPagoService excel;private final InstitucionService instituciones;private final AlcanceDatosService alcance;
 @GetMapping public String listar(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)EstadoConvenioPago estado,@RequestParam(defaultValue="")String texto,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,Authentication a,Model m){List<InstitucionResponse>ins=alcance.filtrarInstituciones(instituciones.listar());if(institucionId==null&&ins.size()==1)institucionId=ins.getFirst().id();m.addAttribute("resultado",service.listar(institucionId,estado,texto,pagina,tamanio));m.addAttribute("instituciones",ins);m.addAttribute("institucionId",institucionId);m.addAttribute("estado",estado);m.addAttribute("estados",EstadoConvenioPago.values());m.addAttribute("texto",texto);m.addAttribute("tamanio",tamanio);menu(m,a);return"admin/convenios-pago";}
 @GetMapping("/nuevo") public String nuevo(Authentication a,Model m){ConvenioPagoForm f=new ConvenioPagoForm();List<InstitucionResponse>ins=alcance.filtrarInstituciones(instituciones.listar());if(ins.size()==1)f.setInstitucionId(ins.getFirst().id());f.setFechaAcuerdo(LocalDate.now());f.setFechaVencimiento(LocalDate.now().plusMonths(1));f.setDescripcion("Convenio de pago");m.addAttribute("form",f);prepararForm(m,a,ins);return"admin/convenio-pago-form";}
 @GetMapping("/excel") public void excel(@RequestParam Long institucionId,@RequestParam(required=false)EstadoConvenioPago estado,@RequestParam(defaultValue="")String texto,HttpServletResponse r)throws IOException{r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=convenios-pago.xlsx");excel.exportar(institucionId,estado,texto,r.getOutputStream());}
 @PostMapping public String crear(@Valid @ModelAttribute("form")ConvenioPagoForm f,BindingResult e,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){prepararForm(m,a,alcance.filtrarInstituciones(instituciones.listar()));return"admin/convenio-pago-form";}try{Long id=service.crear(f);flash.addFlashAttribute("mensaje","Convenio creado; los adeudos seleccionados fueron sustituidos sin perder su historial");return"redirect:/admin/convenios-pago/"+id;}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararForm(m,a,alcance.filtrarInstituciones(instituciones.listar()));return"admin/convenio-pago-form";}}
 @GetMapping(value="/cargos-disponibles",produces=MediaType.APPLICATION_JSON_VALUE) @ResponseBody public List<CargoConvenioOpcion> cargos(@RequestParam Long institucionId,@RequestParam Long tutorId,@RequestParam(defaultValue="")String q){return service.buscarCargos(institucionId,tutorId,q);}
 @GetMapping("/{id}") public String detalle(@PathVariable Long id,Authentication a,Model m){var d=service.detalle(id);m.addAttribute("detalle",d);if(!m.containsAttribute("cancelacion")){CancelarConvenioForm f=new CancelarConvenioForm();f.setVersion(d.version());m.addAttribute("cancelacion",f);}menu(m,a);return"admin/convenio-pago-detalle";}
 @PostMapping("/{id}/cancelar") public String cancelar(@PathVariable Long id,@Valid @ModelAttribute("cancelacion")CancelarConvenioForm f,BindingResult e,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){m.addAttribute("detalle",service.detalle(id));menu(m,a);return"admin/convenio-pago-detalle";}try{service.cancelar(id,f.getVersion(),f.getMotivo());flash.addFlashAttribute("mensaje","Convenio cancelado; los cargos originales volvieron a estar vigentes");return"redirect:/admin/convenios-pago/"+id;}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));m.addAttribute("detalle",service.detalle(id));menu(m,a);return"admin/convenio-pago-detalle";}}
 private void prepararForm(Model m,Authentication a,List<InstitucionResponse>ins){ConvenioPagoForm f=(ConvenioPagoForm)m.getAttribute("form");m.addAttribute("instituciones",ins);m.addAttribute("tutorSeleccionado",service.tutorEtiqueta(f==null?null:f.getTutorId()));m.addAttribute("conceptoSeleccionado",service.conceptoEtiqueta(f==null?null:f.getConceptoCobroId()));m.addAttribute("cargosSeleccionados",service.cargosSeleccionados(f==null?List.of():f.getCargoIds()));menu(m,a);}
 private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.CONVENIOS_PAGO);m.addAttribute("puedeAdministrar",p.contains("CONVENIO_PAGO_ADMINISTRAR"));}
}
