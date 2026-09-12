package escuela.admin.controller;

import escuela.admin.dto.InstitucionForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/instituciones")
public class InstitucionAdminController {
    private final InstitucionService service;
    private final AlcanceDatosService alcance;

    @GetMapping("/nueva")
    String nueva(Model model) {
        alcance.validarNuevaInstitucion();
        preparar(model, new InstitucionForm(), null);
        return "admin/institucion-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") InstitucionForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        alcance.validarNuevaInstitucion();
        if (errores.hasErrors()) { preparar(model, form, null); return "admin/institucion-form"; }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/institucion-form";
        }
        flash.addFlashAttribute("mensaje", "Institución creada correctamente");
        return "redirect:/admin/catalogos/instituciones";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.INSTITUCIONES, id);
        preparar(model, InstitucionForm.desde(service.obtener(id)), id);
        return "admin/institucion-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") InstitucionForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.INSTITUCIONES, id);
        if (errores.hasErrors()) { preparar(model, form, id); return "admin/institucion-form"; }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/institucion-form";
        }
        flash.addFlashAttribute("mensaje", "Cambios guardados correctamente");
        return "redirect:/admin/catalogos/instituciones";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(escuela.admin.dto.ModuloCatalogo.INSTITUCIONES, id);
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, InstitucionForm.desde(service.obtener(id)), id, excepcion);
            return "admin/institucion-form";
        }
        flash.addFlashAttribute("mensaje", "Institución desactivada correctamente");
        return "redirect:/admin/catalogos/instituciones";
    }

    private void preparar(Model model, InstitucionForm form, Long id) {
        model.addAttribute("form", form); model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
    }

    private void prepararError(Model model, InstitucionForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
