package escuela.admin.controller;

import escuela.admin.dto.AlumnoForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.dto.response.AlumnoResponse;
import escuela.alumno.service.AlumnoService;
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
@RequestMapping("/admin/alumnos")
public class AlumnoAdminController {

    private final AlumnoService service;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new AlumnoForm(), null);
        return "admin/alumno-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") AlumnoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/alumno-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Alumno registrado correctamente");
        return "redirect:/admin/catalogos/alumnos";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        preparar(model, AlumnoForm.desde(alumno), id);
        return "admin/alumno-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") AlumnoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/alumno-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Alumno actualizado correctamente");
        return "redirect:/admin/catalogos/alumnos";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.ALUMNOS, id);
        AlumnoResponse alumno = service.obtener(id);
        alcance.validarAdministracionInstitucional(alumno.institucionId());
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, AlumnoForm.desde(alumno), id, excepcion);
            return "admin/alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Alumno desactivado correctamente");
        return "redirect:/admin/catalogos/alumnos";
    }

    private void preparar(Model model, AlumnoForm form, Long id) {
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", alcance.filtrarInstituciones(institucionService.listar()));
    }

    private void prepararError(Model model, AlumnoForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
