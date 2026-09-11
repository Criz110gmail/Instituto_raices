package escuela.admin.controller;

import escuela.admin.dto.InstitucionForm;
import escuela.institucion.service.InstitucionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/instituciones")
public class InstitucionAdminController {
    private final InstitucionService service;

    @GetMapping("/nueva")
    String nueva(Model model) {
        preparar(model, new InstitucionForm(), null);
        return "admin/institucion-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") InstitucionForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) { preparar(model, form, null); return "admin/institucion-form"; }
        service.crear(form.request());
        flash.addFlashAttribute("mensaje", "Institución creada correctamente");
        return "redirect:/admin/catalogos/instituciones";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        preparar(model, InstitucionForm.desde(service.obtener(id)), id);
        return "admin/institucion-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") InstitucionForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) { preparar(model, form, id); return "admin/institucion-form"; }
        service.actualizar(id, form.request());
        flash.addFlashAttribute("mensaje", "Cambios guardados correctamente");
        return "redirect:/admin/catalogos/instituciones";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version, RedirectAttributes flash) {
        service.desactivar(id, version);
        flash.addFlashAttribute("mensaje", "Institución desactivada correctamente");
        return "redirect:/admin/catalogos/instituciones";
    }

    private void preparar(Model model, InstitucionForm form, Long id) {
        model.addAttribute("form", form); model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
    }
}
