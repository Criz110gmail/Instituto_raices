package escuela.admin.controller;

import escuela.admin.dto.CapturaCalificacionesForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.calificacion.dto.HojaCalificacionesResponse;
import escuela.calificacion.repository.CalificacionRepository;
import escuela.calificacion.service.CalificacionService;
import escuela.academico.repository.GrupoRepository;
import escuela.common.exception.ReglaNegocioException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.institucion.service.InstitucionService;
import escuela.inscripcion.repository.AsignacionGrupoRepository;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/calificaciones")
public class CalificacionAdminController {
    private final CalificacionService service;
    private final CalificacionRepository repository;
    private final AsignacionGrupoRepository asignacionRepository;
    private final GrupoRepository grupoRepository;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/captura")
    String captura(@RequestParam(required = false) Long grupoId,
                   @RequestParam(required = false) Long periodoId,
                   @RequestParam(required = false) Long materiaGradoId,
                   Model model) {
        CapturaCalificacionesForm form = new CapturaCalificacionesForm();
        if (grupoId != null || periodoId != null || materiaGradoId != null) {
            try {
                if (grupoId == null || periodoId == null || materiaGradoId == null) {
                    throw new ReglaNegocioException("Selecciona grupo, periodo y materia para abrir la captura");
                }
                alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
                HojaCalificacionesResponse hoja = service.hoja(grupoId, periodoId, materiaGradoId);
                Long institucionId = institucionGrupo(grupoId);
                form = CapturaCalificacionesForm.desde(hoja, institucionId);
                model.addAttribute("hoja", hoja);
            } catch (ReglaNegocioException | RecursoNoEncontradoException excepcion) {
                form.setGrupoId(grupoId);
                form.setPeriodoId(periodoId);
                form.setMateriaGradoId(materiaGradoId);
                model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            }
        }
        preparar(model, form);
        return "admin/calificacion-captura";
    }

    @PostMapping("/captura")
    String guardar(@RequestParam String accion,
                   @Valid @ModelAttribute("form") CapturaCalificacionesForm form,
                   BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            if (form.getGrupoId() == null || form.getPeriodoId() == null
                    || form.getMateriaGradoId() == null) {
                preparar(model, form);
                model.addAttribute("errorOperacion", "La selección está incompleta; vuelve a elegir grupo, periodo y materia");
            } else {
                alcance.validarRecurso(ModuloCatalogo.GRUPOS, form.getGrupoId());
                recargar(model, form);
            }
            return "admin/calificacion-captura";
        }
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, form.getGrupoId());
        boolean publicar = "publicar".equalsIgnoreCase(accion);
        try {
            if (!publicar && !"borrador".equalsIgnoreCase(accion)) {
                throw new ReglaNegocioException("La acción solicitada no es válida");
            }
            service.guardar(form.request(), publicar);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            recargar(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/calificacion-captura";
        }
        flash.addFlashAttribute("mensaje", publicar
                ? "Calificaciones publicadas; ya están disponibles para las familias"
                : "Borrador de calificaciones guardado");
        return redireccion(form);
    }

    @PostMapping("/reabrir")
    String reabrir(@RequestParam Long grupoId, @RequestParam Long periodoId,
                   @RequestParam Long materiaGradoId, RedirectAttributes flash,
                   Model model) {
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
        try {
            service.reabrir(grupoId, periodoId, materiaGradoId);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            CapturaCalificacionesForm form = CapturaCalificacionesForm.desde(
                    service.hoja(grupoId, periodoId, materiaGradoId), institucionGrupo(grupoId));
            recargar(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/calificacion-captura";
        }
        flash.addFlashAttribute("mensaje", "El bloque volvió a borrador y puede corregirse");
        CapturaCalificacionesForm form = new CapturaCalificacionesForm();
        form.setGrupoId(grupoId); form.setPeriodoId(periodoId); form.setMateriaGradoId(materiaGradoId);
        return redireccion(form);
    }

    @GetMapping("/{id}/editar")
    @Transactional(readOnly = true)
    String editar(@PathVariable Long id) {
        alcance.validarRecurso(ModuloCatalogo.CALIFICACIONES, id);
        var calificacion = repository.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("La calificación solicitada no existe"));
        var periodo = calificacion.getPeriodoAcademico();
        var asignaciones = asignacionRepository.buscarParaPeriodo(calificacion.getInscripcion().getId(),
                periodo.getFechaInicio(), periodo.getFechaFin());
        if (asignaciones.isEmpty()) {
            throw new ReglaNegocioException("No se encontró el grupo histórico de esta calificación");
        }
        return "redirect:/admin/calificaciones/captura?grupoId=" + asignaciones.getFirst().getGrupo().getId()
                + "&periodoId=" + periodo.getId()
                + "&materiaGradoId=" + calificacion.getMateriaGrado().getId();
    }

    private void preparar(Model model, CapturaCalificacionesForm form) {
        var instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
    }

    private void recargar(Model model, CapturaCalificacionesForm form) {
        HojaCalificacionesResponse hoja = service.hoja(
                form.getGrupoId(), form.getPeriodoId(), form.getMateriaGradoId());
        model.addAttribute("hoja", hoja);
        preparar(model, form);
    }

    private Long institucionGrupo(Long grupoId) {
        alcance.validarRecurso(ModuloCatalogo.GRUPOS, grupoId);
        return grupoRepository.findInstitucionIdById(grupoId)
                .orElseThrow(() -> new ReglaNegocioException("El grupo solicitado no existe"));
    }

    private String redireccion(CapturaCalificacionesForm form) {
        return "redirect:/admin/calificaciones/captura?grupoId=" + form.getGrupoId()
                + "&periodoId=" + form.getPeriodoId()
                + "&materiaGradoId=" + form.getMateriaGradoId();
    }
}
