package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.service.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.comunicacion.dto.response.AvisoResponse;
import escuela.comunicacion.entity.EstadoAviso;
import escuela.comunicacion.service.AvisoService;
import escuela.common.exception.*;
import escuela.institucion.dto.response.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/avisos")
public class AvisoAdminController {
    private final AvisoService service; private final AvisoConsultaService consulta; private final ExcelAvisoService excel;
    private final InstitucionService institucionService; private final PlantelService plantelService; private final AlcanceDatosService alcance;
    @GetMapping String listado(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(defaultValue="")String texto,@RequestParam(defaultValue="TODOS")String estado,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,Authentication auth,Model model){var ins=instituciones();if(institucionId==null&&!ins.isEmpty())institucionId=ins.getFirst().id();var f=new FiltroAviso(institucionId,plantelId,texto,estado,pagina,tamanio).normalizado();try{model.addAttribute("pagina",consulta.consultar(f));}catch(RuntimeException ex){model.addAttribute("errorFiltro",ex.getMessage());model.addAttribute("pagina",org.springframework.data.domain.Page.empty());}model.addAttribute("filtro",f);model.addAttribute("instituciones",ins);model.addAttribute("planteles",alcance.filtrarPlanteles(plantelService.listar()));model.addAttribute("estados",EstadoAviso.values());navegacion(model,auth);return "admin/avisos";}
    @GetMapping("/excel")void excel(@RequestParam Long institucionId,@RequestParam(required=false)Long plantelId,@RequestParam(defaultValue="")String texto,@RequestParam(defaultValue="TODOS")String estado,HttpServletResponse response)throws IOException{response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");response.setHeader("Content-Disposition","attachment; filename=avisos-escolares.xlsx");excel.exportar(new FiltroAviso(institucionId,plantelId,texto,estado,0,100),response.getOutputStream());}
    @GetMapping("/nuevo")String nuevo(Model m){AvisoForm f=new AvisoForm();var i=instituciones();if(i.size()==1)f.setInstitucionId(i.getFirst().id());preparar(m,f,null,i);return "admin/aviso-form";}
    @PostMapping String crear(@Valid @ModelAttribute("form")AvisoForm f,BindingResult e,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,f,null,instituciones());return "admin/aviso-form";}try{var a=service.crear(f.request());flash.addFlashAttribute("mensaje","Aviso guardado como borrador");return "redirect:/admin/avisos/"+a.id();}catch(ReglaNegocioException|DataIntegrityViolationException|ObjectOptimisticLockingFailureException ex){preparar(m,f,null,instituciones());m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return "admin/aviso-form";}}
    @GetMapping("/{id}")String detalle(@PathVariable Long id,Model m,Authentication a){prepararDetalle(m,service.obtener(id),a);return "admin/aviso-detalle";}
    @GetMapping("/{id}/editar")String editar(@PathVariable Long id,Model m){var a=service.obtener(id);if(a.estado()!=EstadoAviso.BORRADOR)throw new ReglaNegocioException("Sólo los borradores pueden modificarse");preparar(m,AvisoForm.desde(a),id,instituciones());return "admin/aviso-form";}
    @PostMapping("/{id}")String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form")AvisoForm f,BindingResult e,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,f,id,instituciones());return "admin/aviso-form";}try{service.actualizar(id,f.request());flash.addFlashAttribute("mensaje","Borrador actualizado");return "redirect:/admin/avisos/"+id;}catch(ReglaNegocioException|DataIntegrityViolationException|ObjectOptimisticLockingFailureException ex){preparar(m,f,id,instituciones());m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return "admin/aviso-form";}}
    @PostMapping("/{id}/publicar")String publicar(@PathVariable Long id,@RequestParam Long version,Model m,Authentication a,RedirectAttributes flash){try{service.publicar(id,version);flash.addFlashAttribute("mensaje","Aviso publicado");return "redirect:/admin/avisos/"+id;}catch(ReglaNegocioException|DataIntegrityViolationException|ObjectOptimisticLockingFailureException ex){prepararDetalle(m,service.obtener(id),a);m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return "admin/aviso-detalle";}}
    @PostMapping("/{id}/retirar")String retirar(@PathVariable Long id,@Valid @ModelAttribute("retiro")RetiroAvisoForm f,BindingResult e,Model m,Authentication a,RedirectAttributes flash){if(e.hasErrors()){prepararDetalle(m,service.obtener(id),a);return "admin/aviso-detalle";}try{service.retirar(id,f.getVersion(),f.getMotivo());flash.addFlashAttribute("mensaje","Aviso retirado; el historial se conservó");return "redirect:/admin/avisos/"+id;}catch(ReglaNegocioException|DataIntegrityViolationException|ObjectOptimisticLockingFailureException ex){prepararDetalle(m,service.obtener(id),a);m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));return "admin/aviso-detalle";}}
    private void preparar(Model m,AvisoForm f,Long id,List<InstitucionResponse> i){m.addAttribute("form",f);m.addAttribute("id",id);m.addAttribute("edicion",id!=null);m.addAttribute("instituciones",i);m.addAttribute("planteles",alcance.filtrarPlanteles(plantelService.listar()));}
    private void prepararDetalle(Model m,AvisoResponse av,Authentication a){RetiroAvisoForm f=new RetiroAvisoForm();f.setVersion(av.auditoria().version());m.addAttribute("aviso",av);if(!m.containsAttribute("retiro"))m.addAttribute("retiro",f);m.addAttribute("puedeAdministrar",a.getAuthorities().stream().anyMatch(x->x.getAuthority().equals("AVISO_ADMINISTRAR")));}
    private List<InstitucionResponse> instituciones(){return alcance.filtrarInstituciones(institucionService.listar());}
    private void navegacion(Model m,Authentication a){Set<String>p=a.getAuthorities().stream().map(x->x.getAuthority()).collect(Collectors.toSet());m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.AVISOS);m.addAttribute("puedeAdministrar",p.contains("AVISO_ADMINISTRAR"));}
}
