package escuela.admin.controller;

import escuela.academico.service.*;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.calendario.dto.*;
import escuela.calendario.entity.TipoFechaCalendario;
import escuela.calendario.service.*;
import escuela.common.exception.*;
import escuela.institucion.service.*;
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
import java.time.LocalDate;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/calendario-escolar")
public class CalendarioEscolarAdminController {
    private final CalendarioEscolarService service;private final ExcelCalendarioEscolarService excel;
    private final InstitucionService instituciones;private final PlantelService planteles;
    private final CicloEscolarService ciclos;private final NivelEducativoService niveles;private final AlcanceDatosService alcance;

    @GetMapping String listar(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Long cicloEscolarId,
        @RequestParam(required=false)Long plantelId,@RequestParam(required=false)Long nivelEducativoId,
        @RequestParam(required=false)TipoFechaCalendario tipo,@RequestParam(required=false)Boolean activo,
        @RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String texto,
        @RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,Authentication a,Model m){var inst=alcance.filtrarInstituciones(instituciones.listar());if(institucionId==null&&inst.size()==1)institucionId=inst.getFirst().id();FiltroCalendarioEscolar f=new FiltroCalendarioEscolar(institucionId,cicloEscolarId,plantelId,nivelEducativoId,tipo,activo,desde,hasta,texto,pagina,tamanio).normalizado();try{m.addAttribute("resultado",service.listar(f));}catch(RuntimeException ex){m.addAttribute("errorFiltro",MensajeErrorFormulario.desde(ex));m.addAttribute("resultado",org.springframework.data.domain.Page.empty());}m.addAttribute("filtro",f);catalogos(m,inst);menu(m,a);return"admin/calendario-escolar";}
    @GetMapping("/excel") void excel(@RequestParam(required=false)Long institucionId,@RequestParam(required=false)Long cicloEscolarId,@RequestParam(required=false)Long plantelId,@RequestParam(required=false)Long nivelEducativoId,@RequestParam(required=false)TipoFechaCalendario tipo,@RequestParam(required=false)Boolean activo,@RequestParam(required=false)LocalDate desde,@RequestParam(required=false)LocalDate hasta,@RequestParam(defaultValue="")String texto,HttpServletResponse r)throws IOException{r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=calendario-escolar.xlsx");excel.exportar(new FiltroCalendarioEscolar(institucionId,cicloEscolarId,plantelId,nivelEducativoId,tipo,activo,desde,hasta,texto,0,100),r.getOutputStream());}
    @GetMapping("/nuevo") String nuevo(Model m){CalendarioEscolarForm f=new CalendarioEscolarForm();var inst=alcance.filtrarInstituciones(instituciones.listar());if(inst.size()==1)f.setInstitucionId(inst.getFirst().id());f.setFechaInicio(LocalDate.now());f.setFechaFin(LocalDate.now());f.setActivo(true);m.addAttribute("form",f);preparar(m,false,null,inst);return"admin/calendario-escolar-form";}
    @PostMapping String crear(@Valid @ModelAttribute("form")CalendarioEscolarForm f,BindingResult e,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,false,null,alcance.filtrarInstituciones(instituciones.listar()));return"admin/calendario-escolar-form";}try{Long id=service.crear(f);flash.addFlashAttribute("mensaje","Fecha agregada al calendario escolar");return"redirect:/admin/calendario-escolar/"+id+"/editar";}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));preparar(m,false,null,alcance.filtrarInstituciones(instituciones.listar()));return"admin/calendario-escolar-form";}}
    @GetMapping("/{id}/editar") String editar(@PathVariable Long id,Model m){m.addAttribute("form",service.formulario(id));preparar(m,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/calendario-escolar-form";}
    @PostMapping("/{id}") String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form")CalendarioEscolarForm f,BindingResult e,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/calendario-escolar-form";}try{service.actualizar(id,f);flash.addFlashAttribute("mensaje","Calendario actualizado");return"redirect:/admin/calendario-escolar/"+id+"/editar";}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));preparar(m,true,id,alcance.filtrarInstituciones(instituciones.listar()));return"admin/calendario-escolar-form";}}
    private void catalogos(Model m,List<escuela.institucion.dto.response.InstitucionResponse> inst){m.addAttribute("instituciones",inst);m.addAttribute("planteles",alcance.filtrarPlanteles(planteles.listar()));m.addAttribute("niveles",alcance.filtrarNiveles(niveles.listar()));m.addAttribute("ciclos",alcance.filtrarCiclos(inst.stream().flatMap(i->ciclos.listarPorInstitucion(i.id()).stream()).toList()));m.addAttribute("tipos",TipoFechaCalendario.values());}
    private void preparar(Model m,boolean edicion,Long id,List<escuela.institucion.dto.response.InstitucionResponse> inst){m.addAttribute("edicion",edicion);m.addAttribute("id",id);catalogos(m,inst);}
    private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("moduloActual",ModuloCatalogo.CALENDARIO_ESCOLAR);m.addAttribute("puedeAdministrar",p.contains("CALENDARIO_ESCOLAR_ADMINISTRAR"));}
}
