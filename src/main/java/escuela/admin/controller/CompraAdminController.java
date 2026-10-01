package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.compras.dto.*;
import escuela.compras.entity.EstadoCompra;
import escuela.compras.service.*;
import escuela.finanzas.entity.NaturalezaMotivoFinanciero;
import escuela.finanzas.service.MotivoFinancieroService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.*;
import escuela.seguridad.service.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.time.*;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/compras")
public class CompraAdminController {
 private final CompraService service;private final ProveedorService proveedores;private final ExcelCompraService excel;private final MotivoFinancieroService motivos;private final InstitucionService instituciones;private final PlantelService planteles;private final AlcanceDatosService alcance;
 @GetMapping String listar(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(required=false)Long proveedorId,@RequestParam(required=false)Long cuentaId,@RequestParam(required=false)EstadoCompra estado,@RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String texto,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,Authentication a,Model m){var ins=alcance.filtrarInstituciones(instituciones.listar());if(institucionId==null&&ins.size()==1)institucionId=ins.getFirst().id();FiltroCompra f=new FiltroCompra(institucionId,plantelId,proveedorId,cuentaId,estado,desde,hasta,texto,pagina,tamanio).normalizado();try{m.addAttribute("resultado",service.listar(f));}catch(RuntimeException ex){m.addAttribute("errorFiltro",MensajeErrorFormulario.desde(ex));m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}m.addAttribute("filtro",f);m.addAttribute("estados",EstadoCompra.values());catalogos(m,ins,institucionId);menu(m,a);return"admin/compras";}
 @GetMapping("/excel") void excel(@RequestParam Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(required=false)Long proveedorId,@RequestParam(required=false)Long cuentaId,@RequestParam(required=false)EstadoCompra estado,@RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String texto,HttpServletResponse r)throws IOException{r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=compras.xlsx");excel.exportar(new FiltroCompra(institucionId,plantelId,proveedorId,cuentaId,estado,desde,hasta,texto,0,100),r.getOutputStream());}
 @GetMapping("/nueva") String nueva(Authentication a,Model m){CompraForm f=new CompraForm();var ins=alcance.filtrarInstituciones(instituciones.listar());if(ins.size()==1){f.setInstitucionId(ins.getFirst().id());f.setFechaOperacion(LocalDateTime.now(ZoneId.of(ins.getFirst().zonaHoraria())).withSecond(0).withNano(0));}f.getPartidas().add(new CompraPartidaForm());m.addAttribute("form",f);prepararForm(m,a,false,null,ins);return"admin/compra-form";}
 @PostMapping String crear(@Valid @ModelAttribute("form")CompraForm f,BindingResult e,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){prepararForm(m,a,false,null,alcance.filtrarInstituciones(instituciones.listar()));return"admin/compra-form";}try{Long id=service.crear(f);flash.addFlashAttribute("mensaje","Compra guardada como borrador");return"redirect:/admin/compras/"+id;}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararForm(m,a,false,null,alcance.filtrarInstituciones(instituciones.listar()));return"admin/compra-form";}}
 @GetMapping("/{id}/editar") String editar(@PathVariable Long id,Authentication a,Model m){m.addAttribute("form",service.formulario(id));prepararForm(m,a,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/compra-form";}
 @PostMapping("/{id}") String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form")CompraForm f,BindingResult e,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){prepararForm(m,a,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/compra-form";}try{service.actualizar(id,f);flash.addFlashAttribute("mensaje","Borrador actualizado");return"redirect:/admin/compras/"+id;}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararForm(m,a,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/compra-form";}}
 @GetMapping("/{id}") String detalle(@PathVariable Long id,Authentication a,Model m){prepararDetalle(m,a,service.detalle(id));return"admin/compra-detalle";}
 @PostMapping("/{id}/confirmar") String confirmar(@PathVariable Long id,@RequestParam Long version,@AuthenticationPrincipal UsuarioPrincipal p,RedirectAttributes flash){try{service.confirmar(id,version,p);flash.addFlashAttribute("mensaje","Compra confirmada; el egreso ya aparece en Movimientos financieros");}catch(RuntimeException ex){flash.addFlashAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));}return"redirect:/admin/compras/"+id;}
 @PostMapping("/{id}/cancelar") String cancelar(@PathVariable Long id,@Valid @ModelAttribute("cancelacion")CancelarCompraForm f,BindingResult e,@AuthenticationPrincipal UsuarioPrincipal p,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){prepararDetalle(m,a,service.detalle(id));return"admin/compra-detalle";}try{service.cancelar(id,f,p);flash.addFlashAttribute("mensaje","Compra cancelada mediante una reversa financiera");return"redirect:/admin/compras/"+id;}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));prepararDetalle(m,a,service.detalle(id));return"admin/compra-detalle";}}
 private void catalogos(Model m,List<InstitucionResponse> ins,Long institucionId){m.addAttribute("instituciones",ins);m.addAttribute("planteles",alcance.filtrarPlanteles(planteles.listar()));m.addAttribute("proveedores",ins.stream().flatMap(i->proveedores.activos(i.id()).stream()).toList());m.addAttribute("cuentas",ins.stream().flatMap(i->service.cuentasActivas(i.id()).stream()).toList());m.addAttribute("motivos",ins.stream().flatMap(i->motivos.listarActivos(i.id()).stream()).filter(x->x.naturaleza()!=NaturalezaMotivoFinanciero.INGRESO).toList());}
 private void prepararForm(Model m,Authentication a,boolean edicion,Long id,List<InstitucionResponse> ins){CompraForm f=(CompraForm)m.getAttribute("form");m.addAttribute("edicion",edicion);m.addAttribute("id",id);catalogos(m,ins,f==null?null:f.getInstitucionId());menu(m,a);}
 private void prepararDetalle(Model m,Authentication a,CompraDetalleVista d){m.addAttribute("detalle",d);if(!m.containsAttribute("cancelacion")){CancelarCompraForm f=new CancelarCompraForm();f.setVersion(d.version());m.addAttribute("cancelacion",f);}menu(m,a);}
 private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.COMPRAS);m.addAttribute("puedeAdministrar",p.contains("COMPRA_ADMINISTRAR"));m.addAttribute("puedeConfirmar",p.contains("COMPRA_CONFIRMAR"));m.addAttribute("puedeCancelar",p.contains("COMPRA_CANCELAR"));}
}
