package escuela.admin.controller;

import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.PlantelNivelForm;
import escuela.institucion.service.PlantelNivelService;
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
@RequestMapping("/admin/oferta")
public class PlantelNivelAdminController {
    private final PlantelNivelService service;
    private final PlantelService plantelService;
    private final NivelEducativoService nivelService;

    @GetMapping("/nueva")
    String nueva(Model model) {
        preparar(model, new PlantelNivelForm(), null);
        return "admin/oferta-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") PlantelNivelForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/oferta-form";
        }
        service.crear(form.request());
        flash.addFlashAttribute("mensaje", "Oferta educativa creada correctamente");
        return "redirect:/admin/catalogos/oferta";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        preparar(model, PlantelNivelForm.desde(service.obtener(id)), id);
        return "admin/oferta-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") PlantelNivelForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/oferta-form";
        }
        service.actualizar(id, form.request());
        flash.addFlashAttribute("mensaje", "Oferta educativa actualizada correctamente");
        return "redirect:/admin/catalogos/oferta";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      RedirectAttributes flash) {
        service.desactivar(id, version);
        flash.addFlashAttribute("mensaje", "Oferta educativa desactivada correctamente");
        return "redirect:/admin/catalogos/oferta";
    }

    private void preparar(Model model, PlantelNivelForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("planteles", plantelService.listar());
        model.addAttribute("niveles", nivelService.listar());
    }
}
