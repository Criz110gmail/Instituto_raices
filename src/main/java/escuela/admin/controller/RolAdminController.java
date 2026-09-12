package escuela.admin.controller;

import escuela.admin.dto.RolForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.dto.response.RolResponse;
import escuela.seguridad.service.RolService;
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
@RequestMapping("/admin/roles")
public class RolAdminController {

    private final RolService service;
    private final InstitucionService institucionService;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new RolForm(), null);
        return "admin/rol-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") RolForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/rol-form";
        }
        try {
            service.crear(form.request(), form.getPermisoIds());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/rol-form";
        }
        flash.addFlashAttribute("mensaje", "Rol creado correctamente");
        return "redirect:/admin/catalogos/roles";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        RolResponse rol = service.obtener(id);
        preparar(model, RolForm.desde(rol, service.permisosAsignados(id)), id);
        return "admin/rol-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") RolForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/rol-form";
        }
        try {
            service.actualizar(id, form.request(), form.getPermisoIds());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/rol-form";
        }
        flash.addFlashAttribute("mensaje", "Rol actualizado correctamente");
        return "redirect:/admin/catalogos/roles";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            RolResponse rol = service.obtener(id);
            prepararError(model, RolForm.desde(rol, service.permisosAsignados(id)), id, excepcion);
            return "admin/rol-form";
        }
        flash.addFlashAttribute("mensaje", "Rol desactivado correctamente");
        return "redirect:/admin/catalogos/roles";
    }

    private void preparar(Model model, RolForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", institucionService.listar());
        model.addAttribute("permisos", service.listarPermisos());
    }

    private void prepararError(Model model, RolForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
