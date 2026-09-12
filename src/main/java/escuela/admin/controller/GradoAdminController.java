package escuela.admin.controller;

import escuela.academico.dto.response.GradoResponse;
import escuela.academico.dto.response.NivelEducativoResponse;
import escuela.academico.service.GradoService;
import escuela.academico.service.NivelEducativoService;
import escuela.admin.dto.GradoForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
@RequestMapping("/admin/grados")
public class GradoAdminController {
    private final GradoService service;
    private final NivelEducativoService nivelService;
    private final InstitucionService institucionService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new GradoForm(), null);
        return "admin/grado-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") GradoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarRelacion(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/grado-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/grado-form";
        }
        flash.addFlashAttribute("mensaje", "Grado creado correctamente");
        return "redirect:/admin/catalogos/grados";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        GradoResponse grado = service.obtener(id);
        NivelEducativoResponse nivel = nivelService.obtener(grado.nivelEducativoId());
        preparar(model, GradoForm.desde(grado, nivel.institucionId()), id);
        return "admin/grado-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") GradoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        validarRelacion(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/grado-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/grado-form";
        }
        flash.addFlashAttribute("mensaje", "Grado actualizado correctamente");
        return "redirect:/admin/catalogos/grados";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            GradoResponse grado = service.obtener(id);
            NivelEducativoResponse nivel = nivelService.obtener(grado.nivelEducativoId());
            prepararError(model, GradoForm.desde(grado, nivel.institucionId()), id, excepcion);
            return "admin/grado-form";
        }
        flash.addFlashAttribute("mensaje", "Grado desactivado correctamente");
        return "redirect:/admin/catalogos/grados";
    }

    private void preparar(Model model, GradoForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", institucionService.listar());
        model.addAttribute("niveles", nivelService.listar());
    }

    private void validarRelacion(GradoForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getNivelEducativoId() == null) {
            return;
        }
        NivelEducativoResponse nivel = nivelService.obtener(form.getNivelEducativoId());
        if (!nivel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("nivelEducativoId", "grado.nivel.institucion",
                    "El nivel seleccionado no pertenece a la institución indicada");
        }
    }

    private void prepararError(Model model, GradoForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
