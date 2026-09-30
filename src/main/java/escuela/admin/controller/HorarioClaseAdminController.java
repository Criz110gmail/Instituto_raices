package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.horario.dto.*;
import escuela.horario.service.*;
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
import java.time.*;
import java.util.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/horarios-clases")
public class HorarioClaseAdminController {
    private final HorarioClaseService service;private final ExcelHorarioClaseService excel;
    @GetMapping String listar(@RequestParam(required=false)Long maestroId,@RequestParam(defaultValue="")String maestroTexto,
        @RequestParam(required=false)Long grupoId,@RequestParam(defaultValue="")String grupoTexto,
        @RequestParam(required=false)Integer diaSemana,@RequestParam(required=false)LocalDate fecha,
        @RequestParam(required=false)Boolean activo,@RequestParam(defaultValue="0")int pagina,@RequestParam(defaultValue="25")int tamanio,
        Authentication a,Model m){FiltroHorario f=new FiltroHorario(maestroId,grupoId,diaSemana,fecha,activo,pagina,tamanio).normalizado();m.addAttribute("filtro",f);m.addAttribute("maestroTexto",maestroTexto);m.addAttribute("grupoTexto",grupoTexto);m.addAttribute("resultado",service.listar(f));menu(m,a);return"admin/horarios-clases";}
    @GetMapping("/excel") void excel(@RequestParam(required=false)Long maestroId,@RequestParam(required=false)Long grupoId,@RequestParam(required=false)Integer diaSemana,@RequestParam(required=false)LocalDate fecha,@RequestParam(required=false)Boolean activo,HttpServletResponse r)throws IOException{r.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");r.setHeader("Content-Disposition","attachment; filename=horarios-clases.xlsx");excel.exportar(new FiltroHorario(maestroId,grupoId,diaSemana,fecha,activo,0,100),r.getOutputStream());}
    @GetMapping("/nuevo") String nuevo(Authentication a,Model m){HorarioClaseForm f=new HorarioClaseForm();f.setFechaInicio(LocalDate.now());f.setFechaFin(LocalDate.now().plusMonths(5));f.setHoraInicio(LocalTime.of(8,0));f.setHoraFin(LocalTime.of(9,0));m.addAttribute("form",f);preparar(m,a,false,null,"");return"admin/horario-clase-form";}
    @PostMapping String crear(@Valid @ModelAttribute("form")HorarioClaseForm f,BindingResult e,@RequestParam(defaultValue="")String asignacionTexto,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,a,false,null,asignacionTexto);return"admin/horario-clase-form";}try{Long id=service.crear(f);flash.addFlashAttribute("mensaje","Bloque de clase registrado");return"redirect:/admin/horarios-clases/"+id+"/editar";}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));preparar(m,a,false,null,asignacionTexto);return"admin/horario-clase-form";}}
    @GetMapping("/{id}/editar") String editar(@PathVariable Long id,Authentication a,Model m){HorarioClaseForm f=service.formulario(id);m.addAttribute("form",f);preparar(m,a,true,id,service.etiquetaAsignacion(f.getAsignacionMaestroId()));return"admin/horario-clase-form";}
    @PostMapping("/{id}") String actualizar(@PathVariable Long id,@Valid @ModelAttribute("form")HorarioClaseForm f,BindingResult e,@RequestParam(defaultValue="")String asignacionTexto,Authentication a,Model m,RedirectAttributes flash){if(e.hasErrors()){preparar(m,a,true,id,asignacionTexto);return"admin/horario-clase-form";}try{service.actualizar(id,f);flash.addFlashAttribute("mensaje","Horario actualizado");return"redirect:/admin/horarios-clases/"+id+"/editar";}catch(RuntimeException ex){m.addAttribute("errorOperacion",MensajeErrorFormulario.desde(ex));preparar(m,a,true,id,asignacionTexto);return"admin/horario-clase-form";}}
    @GetMapping("/asignaciones") @ResponseBody escuela.admin.dto.ResultadoAutocompletado asignaciones(@RequestParam(defaultValue="")String q){return service.buscarAsignaciones(q);}
    private void preparar(Model m,Authentication a,boolean edicion,Long id,String texto){m.addAttribute("edicion",edicion);m.addAttribute("id",id);m.addAttribute("asignacionTexto",texto);menu(m,a);}
    private void menu(Model m,Authentication a){Set<String>p=new HashSet<>();a.getAuthorities().forEach(x->p.add(x.getAuthority()));m.addAttribute("modulos",Arrays.stream(ModuloCatalogo.values()).filter(x->x.visibleCon(p)).toList());m.addAttribute("puedeAdministrar",p.contains("HORARIO_CLASE_ADMINISTRAR"));m.addAttribute("moduloActual",ModuloCatalogo.HORARIOS_CLASE);}
}
