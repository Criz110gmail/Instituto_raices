package escuela.admin.controller;

import escuela.admin.dto.PlantelForm;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/admin/planteles")
public class PlantelAdminController {
    private final PlantelService service;
    private final InstitucionService institucionService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new PlantelForm(), null);
        return "admin/plantel-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") PlantelForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/plantel-form";
        }
        service.crear(form.request());
        flash.addFlashAttribute("mensaje", "Plantel creado correctamente");
        return "redirect:/admin/catalogos/planteles";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        preparar(model, PlantelForm.desde(service.obtener(id)), id);
        return "admin/plantel-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") PlantelForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/plantel-form";
        }
        service.actualizar(id, form.request());
        flash.addFlashAttribute("mensaje", "Plantel actualizado correctamente");
        return "redirect:/admin/catalogos/planteles";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      RedirectAttributes flash) {
        service.desactivar(id, version);
        flash.addFlashAttribute("mensaje", "Plantel desactivado correctamente");
        return "redirect:/admin/catalogos/planteles";
    }

    private void preparar(Model model, PlantelForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", institucionService.listar());
    }
}
