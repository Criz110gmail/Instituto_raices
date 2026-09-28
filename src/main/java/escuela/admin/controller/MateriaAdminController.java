package escuela.admin.controller;

import escuela.academico.dto.response.MateriaGradoResponse;
import escuela.academico.dto.response.MateriaResponse;
import escuela.academico.entity.TipoEvaluacion;
import escuela.academico.service.MateriaService;
import escuela.admin.dto.MateriaForm;
import escuela.admin.dto.MateriaGradoForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/materias")
public class MateriaAdminController {
    private final MateriaService service;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nueva")
    String nueva(Model model) {
        prepararMateria(model, new MateriaForm(), null);
        return "admin/materia-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") MateriaForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarInstitucion(form.getInstitucionId());
        if (errores.hasErrors()) {
            prepararMateria(model, form, null);
            return "admin/materia-form";
        }
        try {
            MateriaResponse materia = service.crear(form.request());
            flash.addFlashAttribute("mensaje", "Materia creada; ahora configura los grados donde se imparte");
            return "redirect:/admin/materias/" + materia.id() + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararMateria(model, form, null);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/materia-form";
        }
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        MateriaResponse materia = materiaAutorizada(id);
        prepararMateria(model, MateriaForm.desde(materia), id);
        return "admin/materia-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") MateriaForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        materiaAutorizada(id);
        validarInstitucion(form.getInstitucionId());
        if (errores.hasErrors()) {
            prepararMateria(model, form, id);
            return "admin/materia-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararMateria(model, form, id);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/materia-form";
        }
        flash.addFlashAttribute("mensaje", "Materia actualizada correctamente");
        return "redirect:/admin/materias/" + id + "/editar";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        MateriaResponse materia = materiaAutorizada(id);
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararMateria(model, MateriaForm.desde(materia), id);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/materia-form";
        }
        flash.addFlashAttribute("mensaje", "Materia y planes activos desactivados correctamente");
        return "redirect:/admin/catalogos/materias";
    }

    @GetMapping("/{materiaId}/planes/nuevo")
    String nuevoPlan(@PathVariable Long materiaId, Model model) {
        MateriaResponse materia = materiaAutorizada(materiaId);
        prepararPlan(model, materia, new MateriaGradoForm(), null);
        return "admin/materia-plan-form";
    }

    @PostMapping("/{materiaId}/planes")
    String crearPlan(@PathVariable Long materiaId,
                     @Valid @ModelAttribute("form") MateriaGradoForm form,
                     BindingResult errores, Model model, RedirectAttributes flash) {
        MateriaResponse materia = materiaAutorizada(materiaId);
        if (form.getGradoId() != null) alcance.validarRecurso(ModuloCatalogo.GRADOS, form.getGradoId());
        normalizarEscalaCualitativa(form);
        if (errores.hasErrors()) {
            prepararPlan(model, materia, form, null);
            return "admin/materia-plan-form";
        }
        try {
            service.asignarGrado(materiaId, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararPlan(model, materia, form, null);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/materia-plan-form";
        }
        flash.addFlashAttribute("mensaje", "Grado agregado al plan de la materia");
        return "redirect:/admin/materias/" + materiaId + "/editar#plan-grados";
    }

    @GetMapping("/{materiaId}/planes/{planId}/editar")
    String editarPlan(@PathVariable Long materiaId, @PathVariable Long planId, Model model) {
        MateriaResponse materia = materiaAutorizada(materiaId);
        MateriaGradoResponse plan = plan(materiaId, planId);
        prepararPlan(model, materia, MateriaGradoForm.desde(plan), planId);
        return "admin/materia-plan-form";
    }

    @PostMapping("/{materiaId}/planes/{planId}")
    String actualizarPlan(@PathVariable Long materiaId, @PathVariable Long planId,
                          @Valid @ModelAttribute("form") MateriaGradoForm form,
                          BindingResult errores, Model model, RedirectAttributes flash) {
        MateriaResponse materia = materiaAutorizada(materiaId);
        normalizarEscalaCualitativa(form);
        if (errores.hasErrors()) {
            prepararPlan(model, materia, form, planId);
            return "admin/materia-plan-form";
        }
        try {
            service.actualizarGrado(materiaId, planId, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararPlan(model, materia, form, planId);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/materia-plan-form";
        }
        flash.addFlashAttribute("mensaje", "Configuración de evaluación actualizada");
        return "redirect:/admin/materias/" + materiaId + "/editar#plan-grados";
    }

    @PostMapping("/{materiaId}/planes/{planId}/desactivar")
    String desactivarPlan(@PathVariable Long materiaId, @PathVariable Long planId,
                          @RequestParam Long version, Model model, RedirectAttributes flash) {
        MateriaResponse materia = materiaAutorizada(materiaId);
        try {
            service.desactivarGrado(materiaId, planId, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararMateria(model, MateriaForm.desde(materia), materiaId);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/materia-form";
        }
        flash.addFlashAttribute("mensaje", "Plan del grado desactivado; su historial se conserva");
        return "redirect:/admin/materias/" + materiaId + "/editar#plan-grados";
    }

    private MateriaResponse materiaAutorizada(Long id) {
        alcance.validarRecurso(ModuloCatalogo.MATERIAS, id);
        MateriaResponse materia = service.obtener(id);
        alcance.validarAdministracionInstitucional(materia.institucionId());
        return materia;
    }

    private MateriaGradoResponse plan(Long materiaId, Long planId) {
        return service.listarPlanes(materiaId).stream().filter(item -> item.id().equals(planId))
                .findFirst().orElseThrow(() -> new ReglaNegocioException("El plan solicitado no pertenece a la materia"));
    }

    private void prepararMateria(Model model, MateriaForm form, Long id) {
        var instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        if (id != null) model.addAttribute("planes", service.listarPlanes(id));
    }

    private void prepararPlan(Model model, MateriaResponse materia, MateriaGradoForm form, Long planId) {
        model.addAttribute("form", form);
        model.addAttribute("materia", materia);
        model.addAttribute("planId", planId);
        model.addAttribute("edicion", planId != null);
        model.addAttribute("tiposEvaluacion", TipoEvaluacion.values());
    }

    private void validarInstitucion(Long institucionId) {
        if (institucionId != null) alcance.validarAdministracionInstitucional(institucionId);
    }

    private void normalizarEscalaCualitativa(MateriaGradoForm form) {
        if (form.getTipoEvaluacion() == TipoEvaluacion.CUALITATIVA) {
            form.setEscalaMinima(null);
            form.setEscalaMaxima(null);
            form.setMinimaAprobatoria(null);
            form.setDecimales(0);
        }
    }
}
