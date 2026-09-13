package escuela.admin.controller;

import escuela.admin.dto.AlumnoTutorForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.alumno.dto.response.AlumnoTutorResponse;
import escuela.alumno.service.AlumnoService;
import escuela.alumno.service.AlumnoTutorService;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
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
import java.util.stream.Stream;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/vinculos-tutor")
public class AlumnoTutorAdminController {

    private final AlumnoTutorService service;
    private final AlumnoService alumnoService;
    private final TutorService tutorService;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new AlumnoTutorForm(), null);
        return "admin/alumno-tutor-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") AlumnoTutorForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/alumno-tutor-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/alumno-tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Vínculo registrado correctamente");
        return "redirect:/admin/catalogos/vinculos-tutor";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.VINCULOS_TUTOR, id);
        AlumnoTutorResponse vinculo = service.obtener(id);
        alcance.validarAdministracionInstitucional(vinculo.institucionId());
        preparar(model, AlumnoTutorForm.desde(vinculo), id);
        return "admin/alumno-tutor-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") AlumnoTutorForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.VINCULOS_TUTOR, id);
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/alumno-tutor-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/alumno-tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Vínculo actualizado correctamente");
        return "redirect:/admin/catalogos/vinculos-tutor";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.VINCULOS_TUTOR, id);
        AlumnoTutorResponse vinculo = service.obtener(id);
        alcance.validarAdministracionInstitucional(vinculo.institucionId());
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, AlumnoTutorForm.desde(vinculo), id, excepcion);
            return "admin/alumno-tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Vínculo revocado correctamente");
        return "redirect:/admin/catalogos/vinculos-tutor";
    }

    private void validarAlcance(AlumnoTutorForm form) {
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
        if (form.getAlumnoId() != null) alcance.validarRecurso(ModuloCatalogo.ALUMNOS, form.getAlumnoId());
        if (form.getTutorId() != null) alcance.validarRecurso(ModuloCatalogo.TUTORES, form.getTutorId());
    }

    private void preparar(Model model, AlumnoTutorForm form, Long id) {
        List<InstitucionResponse> instituciones =
                alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("alumnoSeleccionado", etiquetaAlumno(form.getAlumnoId()));
        model.addAttribute("tutorSeleccionado", etiquetaTutor(form.getTutorId()));
    }

    private void prepararError(Model model, AlumnoTutorForm form, Long id,
                               RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }

    private String etiquetaAlumno(Long alumnoId) {
        if (alumnoId == null) return "";
        var alumno = alumnoService.obtener(alumnoId);
        return alumno.matricula() + " · " + nombre(alumno.nombres(), alumno.primerApellido(),
                alumno.segundoApellido());
    }

    private String etiquetaTutor(Long tutorId) {
        if (tutorId == null) return "";
        var tutor = tutorService.obtener(tutorId);
        return nombre(tutor.nombres(), tutor.primerApellido(), tutor.segundoApellido());
    }

    private String nombre(String... partes) {
        return Stream.of(partes)
                .filter(parte -> parte != null && !parte.isBlank())
                .collect(java.util.stream.Collectors.joining(" "));
    }
}
