package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.TutorForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioService;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.service.TutorService;
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

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tutores")
public class TutorAdminController {

    private final TutorService service;
    private final InstitucionService institucionService;
    private final UsuarioService usuarioService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new TutorForm(), null);
        return "admin/tutor-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") TutorForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/tutor-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Tutor registrado correctamente");
        return "redirect:/admin/catalogos/tutores";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        TutorResponse tutor = service.obtener(id);
        alcance.validarAdministracionInstitucional(tutor.institucionId());
        preparar(model, TutorForm.desde(tutor), id);
        return "admin/tutor-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") TutorForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/tutor-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Tutor actualizado correctamente");
        return "redirect:/admin/catalogos/tutores";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        TutorResponse tutor = service.obtener(id);
        alcance.validarAdministracionInstitucional(tutor.institucionId());
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, TutorForm.desde(tutor), id, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Tutor desactivado correctamente");
        return "redirect:/admin/catalogos/tutores";
    }

    private void validarAlcance(TutorForm form) {
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
        if (form.getUsuarioId() != null) {
            alcance.validarRecurso(ModuloCatalogo.USUARIOS, form.getUsuarioId());
        }
    }

    private void preparar(Model model, TutorForm form, Long id) {
        List<InstitucionResponse> instituciones =
                alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        List<UsuarioResponse> usuarios = instituciones.stream()
                .flatMap(institucion -> usuarioService.listarPorInstitucion(institucion.id()).stream())
                .filter(usuario -> usuario.estado() != EstadoUsuario.INACTIVO)
                .toList();
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("usuarios", usuarios);
    }

    private void prepararError(Model model, TutorForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
