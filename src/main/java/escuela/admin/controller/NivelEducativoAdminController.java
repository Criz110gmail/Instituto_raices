package escuela.admin.controller;

import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.NivelEducativoForm;
import escuela.institucion.service.InstitucionService;
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
@RequestMapping("/admin/niveles")
public class NivelEducativoAdminController {
    private final NivelEducativoService service;
    private final InstitucionService institucionService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new NivelEducativoForm(), null);
        return "admin/nivel-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") NivelEducativoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/nivel-form";
        }
        service.crear(form.request());
        flash.addFlashAttribute("mensaje", "Nivel educativo creado correctamente");
        return "redirect:/admin/catalogos/niveles";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        preparar(model, NivelEducativoForm.desde(service.obtener(id)), id);
        return "admin/nivel-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") NivelEducativoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/nivel-form";
        }
        service.actualizar(id, form.request());
        flash.addFlashAttribute("mensaje", "Nivel educativo actualizado correctamente");
        return "redirect:/admin/catalogos/niveles";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      RedirectAttributes flash) {
        service.desactivar(id, version);
        flash.addFlashAttribute("mensaje", "Nivel educativo desactivado correctamente");
        return "redirect:/admin/catalogos/niveles";
    }

    private void preparar(Model model, NivelEducativoForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", institucionService.listar());
    }
}
