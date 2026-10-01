package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.compras.dto.*;
import escuela.compras.service.*;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/proveedores")
public class ProveedorAdminController {
 private final ProveedorService service;private final ExcelProveedorService excel;private final InstitucionService instituciones;private final AlcanceDatosService alcance;
 @GetMapping String listar(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Boolean activo,@RequestParam(defaultValue="")String texto,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,Authentication a,Model m){var ins=alcance.filtrarInstituciones(instituciones.listar());if(institucionId==null&&ins.size()==1)institucionId=ins.getFirst().id();FiltroProveedor f=new FiltroProveedor(institucionId,activo,texto,pagina,tamanio).normalizado();try{m.addAttribute("resultado",service.listar(f));}catch(RuntimeException ex){m.addAttribute("errorFiltro",MensajeErrorFormulario.desde(ex));m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}m.addAttribute("filtro",f);m.addAttribute("instituciones",ins);menu(m,a);return"admin/proveedores";}
 @GetMapping("/excel") void excel(@RequestParam Long institucionId,@RequestParam(required=false)Boolean activo,@RequestParam(defaultValue="")String texto,HttpServletResponse r)throws IOException{r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=proveedores.xlsx");excel.exportar(new FiltroProveedor(institucionId,activo,texto,0,100),r.getOutputStream());}
 @GetMapping("/nuevo") String nuevo(Authentication a,Model m){ProveedorForm f=new ProveedorForm();var ins=alcance.filtrarInstituciones(instituciones.listar());if(ins.size()==1)f.setInstitucionId(ins.getFirst().id());m.addAttribute("form",f);preparar(m,a,false,null,ins);return"admin/proveedor-form";}
 @PostMapping String crear(@Valid @ModelAttribute("form")ProveedorForm f,BindingResult e,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,a,false,null,alcance.filtrarInstituciones(instituciones.listar()));return"admin/proveedor-form";}try{Long id=service.crear(f);flash.addFlashAttribute("mensaje","Proveedor registrado");return"redirect:/admin/proveedores/"+id+"/editar";}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));preparar(m,a,false,null,alcance.filtrarInstituciones(instituciones.listar()));return"admin/proveedor-form";}}
 @GetMapping("/{id}/editar") String editar(@PathVariable Long id,Authentication a,Model m){m.addAttribute("form",service.formulario(id));preparar(m,a,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/proveedor-form";}
 @PostMapping("/{id}") String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form")ProveedorForm f,BindingResult e,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,a,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/proveedor-form";}try{service.actualizar(id,f);flash.addFlashAttribute("mensaje","Proveedor actualizado");return"redirect:/admin/proveedores/"+id+"/editar";}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));preparar(m,a,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/proveedor-form";}}
 private void preparar(Model m,Authentication a,boolean edicion,Long id,List<?> ins){m.addAttribute("edicion",edicion);m.addAttribute("id",id);m.addAttribute("instituciones",ins);menu(m,a);}private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.PROVEEDORES);m.addAttribute("puedeAdministrar",p.contains("PROVEEDOR_ADMINISTRAR"));}
}
