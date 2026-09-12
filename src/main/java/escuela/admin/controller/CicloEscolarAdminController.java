package escuela.admin.controller;

import escuela.academico.entity.EstadoAcademico;
import escuela.academico.service.CicloEscolarService;
import escuela.admin.dto.CicloEscolarForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.admin.dto.ModuloCatalogo;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/ciclos")
public class CicloEscolarAdminController {
    private final CicloEscolarService service;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new CicloEscolarForm(), null);
        return "admin/ciclo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") CicloEscolarForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/ciclo-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/ciclo-form";
        }
        flash.addFlashAttribute("mensaje", "Ciclo escolar creado correctamente");
        return "redirect:/admin/catalogos/ciclos";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.CICLOS, id);
        preparar(model, CicloEscolarForm.desde(service.obtener(id)), id);
        return "admin/ciclo-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") CicloEscolarForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CICLOS, id);
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/ciclo-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/ciclo-form";
        }
        flash.addFlashAttribute("mensaje", "Ciclo escolar actualizado correctamente");
        return "redirect:/admin/catalogos/ciclos";
    }

    private void preparar(Model model, CicloEscolarForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", alcance.filtrarInstituciones(institucionService.listar()));
        model.addAttribute("estados", EstadoAcademico.values());
    }

    private void prepararError(Model model, CicloEscolarForm form, Long id,
                               RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
