package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.asistencia.dto.HojaAsistenciaResponse;
import escuela.asistencia.entity.EstadoAsistencia;
import escuela.asistencia.repository.AsistenciaRepository;
import escuela.asistencia.service.AsistenciaService;
import escuela.academico.repository.GrupoRepository;
import escuela.common.exception.*;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/asistencia")
public class AsistenciaAdminController {
    private final AsistenciaService service;
    private final AsistenciaRepository repository;
    private final GrupoRepository grupos;
    private final InstitucionService instituciones;
    private final AlcanceDatosService alcance;

    @GetMapping("/captura")
    String captura(@RequestParam(required=false) Long grupoId,
                   @RequestParam(required=false) LocalDate fecha, Model model) {
        CapturaAsistenciaForm form = new CapturaAsistenciaForm();
        form.setFecha(fecha == null ? LocalDate.now() : fecha);
        if (grupoId != null) {
            try {
                alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
                HojaAsistenciaResponse hoja = service.hoja(grupoId, form.getFecha());
                form = CapturaAsistenciaForm.desde(hoja, institucionGrupo(grupoId));
                model.addAttribute("hoja", hoja);
            } catch (ReglaNegocioException | RecursoNoEncontradoException ex) {
                form.setGrupoId(grupoId); model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            }
        }
        preparar(model, form); return "admin/asistencia-captura";
    }

    @PostMapping("/captura")
    String guardar(@Valid @ModelAttribute("form") CapturaAsistenciaForm form, BindingResult errores,
                   Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            recargarSegura(model, form);
            model.addAttribute("errorOperacion", "Revisa que todos los alumnos tengan estado y que las observaciones no superen 1000 caracteres");
            return "admin/asistencia-captura";
        }
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, form.getGrupoId());
        try { service.guardar(form.request()); }
        catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            recargarSegura(model, form); model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            return "admin/asistencia-captura";
        }
        flash.addFlashAttribute("mensaje", "Asistencia guardada correctamente");
        return "redirect:/admin/asistencia/captura?grupoId="+form.getGrupoId()+"&fecha="+form.getFecha();
    }

    @GetMapping("/{id}/editar") @Transactional(readOnly=true)
    String editar(@PathVariable Long id) {
        alcance.validarRecurso(ModuloCatalogo.ASISTENCIA, id);
        var a=repository.findById(id).orElseThrow(()->new ReglaNegocioException("La asistencia no existe"));
        return "redirect:/admin/asistencia/captura?grupoId="+a.getGrupo().getId()+"&fecha="+a.getFecha();
    }

    private void preparar(Model model, CapturaAsistenciaForm form) {
        var lista=alcance.filtrarInstituciones(instituciones.listar());
        if(form.getInstitucionId()==null&&lista.size()==1) form.setInstitucionId(lista.getFirst().id());
        model.addAttribute("form",form); model.addAttribute("instituciones",lista); model.addAttribute("estados", EstadoAsistencia.values());
    }
    private void recargarSegura(Model model,CapturaAsistenciaForm form){
        if(form.getGrupoId()!=null&&form.getFecha()!=null){
            try{
                HojaAsistenciaResponse hoja=service.hoja(form.getGrupoId(),form.getFecha());
                fusionarFilas(form,hoja);
                model.addAttribute("hoja",hoja);
            }catch(RuntimeException ignored){}
        }
        preparar(model,form);
    }
    private void fusionarFilas(CapturaAsistenciaForm form,HojaAsistenciaResponse hoja){
        Map<Long,FilaAsistenciaForm> enviadas=form.getFilas().stream()
                .filter(f->f.getInscripcionId()!=null)
                .collect(Collectors.toMap(FilaAsistenciaForm::getInscripcionId, Function.identity(),(a,b)->a));
        form.setFilas(hoja.filas().stream().map(f->{
            FilaAsistenciaForm enviada=enviadas.get(f.inscripcionId());
            if(enviada==null) return FilaAsistenciaForm.desde(f);
            enviada.setAsistenciaId(f.asistenciaId());
            enviada.setMatricula(f.matricula());
            enviada.setAlumno(f.alumno());
            enviada.setVersion(f.version());
            return enviada;
        }).toList());
    }
    private Long institucionGrupo(Long id){return grupos.findInstitucionIdById(id).orElseThrow(()->new ReglaNegocioException("El grupo no existe"));}
}
