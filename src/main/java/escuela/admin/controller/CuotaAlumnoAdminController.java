package escuela.admin.controller;

import escuela.admin.dto.CuotaAlumnoForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.dto.response.CuotaAlumnoResponse;
import escuela.cobranza.entity.EstadoCuota;
import escuela.cobranza.entity.FrecuenciaCuota;
import escuela.cobranza.service.ConceptoCobroService;
import escuela.cobranza.service.CuotaAlumnoService;
import escuela.common.exception.ReglaNegocioException;
import escuela.inscripcion.service.InscripcionService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.dto.response.PlantelResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cuotas-alumno")
public class CuotaAlumnoAdminController {
    private final CuotaAlumnoService service;
    private final ConceptoCobroService conceptoService;
    private final InscripcionService inscripcionService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new CuotaAlumnoForm(), null);
        return "admin/cuota-alumno-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") CuotaAlumnoForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/cuota-alumno-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/cuota-alumno-form";
        }
        flash.addFlashAttribute("mensaje", form.isGeneracionAutomatica()
                ? "Cuota creada; quedó preparada para generación automática de cargos"
                : "Cuota creada con generación manual");
        return "redirect:/admin/catalogos/cuotas-alumno";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.CUOTAS_ALUMNO, id);
        preparar(model, CuotaAlumnoForm.desde(service.obtener(id)), id);
        return "admin/cuota-alumno-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") CuotaAlumnoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CUOTAS_ALUMNO, id);
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/cuota-alumno-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/cuota-alumno-form";
        }
        flash.addFlashAttribute("mensaje", "Configuración de cuota actualizada correctamente");
        return "redirect:/admin/catalogos/cuotas-alumno";
    }

    private void preparar(Model model, CuotaAlumnoForm form, Long id) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        if (form.getPlantelId() == null && planteles.size() == 1) {
            form.setPlantelId(planteles.getFirst().id());
        }
        if ((form.getMoneda() == null || form.getMoneda().isBlank()) && form.getInstitucionId() != null) {
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .map(InstitucionResponse::monedaPredeterminada).ifPresent(form::setMoneda);
        }
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("frecuencias", FrecuenciaCuota.values());
        model.addAttribute("estados", EstadoCuota.values());
        model.addAttribute("inscripcionSeleccionada", etiquetaInscripcion(form.getInscripcionId()));
        model.addAttribute("conceptoSeleccionado", etiquetaConcepto(form.getConceptoCobroId()));
    }

    private void validarAlcance(CuotaAlumnoForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() != null) alcance.validarPlantel(form.getPlantelId());
        if (form.getInscripcionId() != null) {
            alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES, form.getInscripcionId());
        }
        if (form.getConceptoCobroId() != null) {
            alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO, form.getConceptoCobroId());
        }
    }

    private void validarRelaciones(CuotaAlumnoForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getPlantelId() == null
                || form.getInscripcionId() == null || form.getConceptoCobroId() == null) return;
        var inscripcion = inscripcionService.obtener(form.getInscripcionId());
        var concepto = conceptoService.obtener(form.getConceptoCobroId());
        if (!inscripcion.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("inscripcionId", "cuota.inscripcion.institucion",
                    "La inscripción no pertenece a la institución indicada");
        }
        if (!inscripcion.plantelId().equals(form.getPlantelId())) {
            errores.rejectValue("inscripcionId", "cuota.inscripcion.plantel",
                    "La inscripción no pertenece al plantel indicado");
        }
        if (!concepto.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("conceptoCobroId", "cuota.concepto.institucion",
                    "El concepto no pertenece a la institución indicada");
        }
    }

    private String etiquetaInscripcion(Long id) {
        if (id == null) return "";
        var inscripcion = inscripcionService.obtener(id);
        return inscripcion.numeroInscripcion() + " · " + inscripcion.alumnoNombre();
    }

    private String etiquetaConcepto(Long id) {
        if (id == null) return "";
        var concepto = conceptoService.obtener(id);
        return concepto.codigo() + " · " + concepto.nombre();
    }

    private void prepararError(Model model, CuotaAlumnoForm form, Long id,
                               RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
