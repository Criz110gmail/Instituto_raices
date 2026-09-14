package escuela.admin.controller;

import escuela.admin.dto.ConceptoCobroForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.dto.response.ConceptoCobroResponse;
import escuela.cobranza.entity.CategoriaConceptoCobro;
import escuela.cobranza.service.ConceptoCobroService;
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
@RequestMapping("/admin/conceptos-cobro")
public class ConceptoCobroAdminController {
    private final ConceptoCobroService service;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new ConceptoCobroForm(), null);
        return "admin/concepto-cobro-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") ConceptoCobroForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/concepto-cobro-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/concepto-cobro-form";
        }
        flash.addFlashAttribute("mensaje", "Concepto de cobro creado correctamente");
        return "redirect:/admin/catalogos/conceptos-cobro";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO, id);
        ConceptoCobroResponse concepto = service.obtener(id);
        alcance.validarAdministracionInstitucional(concepto.institucionId());
        preparar(model, ConceptoCobroForm.desde(concepto), id);
        return "admin/concepto-cobro-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") ConceptoCobroForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO, id);
        alcance.validarAdministracionInstitucional(service.obtener(id).institucionId());
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/concepto-cobro-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/concepto-cobro-form";
        }
        flash.addFlashAttribute("mensaje", "Concepto de cobro actualizado correctamente");
        return "redirect:/admin/catalogos/conceptos-cobro";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO, id);
        alcance.validarAdministracionInstitucional(service.obtener(id).institucionId());
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            ConceptoCobroResponse concepto = service.obtener(id);
            prepararError(model, ConceptoCobroForm.desde(concepto), id, excepcion);
            return "admin/concepto-cobro-form";
        }
        flash.addFlashAttribute("mensaje", "Concepto de cobro desactivado correctamente");
        return "redirect:/admin/catalogos/conceptos-cobro";
    }

    private void preparar(Model model, ConceptoCobroForm form, Long id) {
        var instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("categorias", CategoriaConceptoCobro.values());
    }

    private void validarAlcance(ConceptoCobroForm form) {
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
    }

    private void prepararError(Model model, ConceptoCobroForm form, Long id,
                               RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
