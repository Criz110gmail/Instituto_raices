package escuela.admin.controller;

import escuela.academico.service.PeriodoAcademicoService;
import escuela.admin.dto.CargoForm;
import escuela.admin.dto.GeneracionCargosForm;
import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.service.CargoService;
import escuela.cobranza.service.ConceptoCobroService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cargos")
public class CargoAdminController {

    private final CargoService service;
    private final ConceptoCobroService conceptoService;
    private final InscripcionService inscripcionService;
    private final PeriodoAcademicoService periodoService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        prepararManual(model, new CargoForm());
        return "admin/cargo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") CargoForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarAlcance(form);
        validarRelaciones(form, errores);
        if (errores.hasErrors()) {
            prepararManual(model, form);
            return "admin/cargo-form";
        }
        try {
            service.crearManual(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararManual(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/cargo-form";
        }
        flash.addFlashAttribute("mensaje", "Cargo individual emitido correctamente");
        return "redirect:/admin/catalogos/cargos";
    }

    @GetMapping("/{id}/editar")
    String detalle(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS, id);
        model.addAttribute("cargo", service.obtener(id));
        return "admin/cargo-detalle";
    }

    @PostMapping("/{id}/cancelar")
    String cancelar(@PathVariable Long id, @RequestParam Long version,
                    @RequestParam String motivo, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS, id);
        try {
            service.cancelar(id, version, motivo);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            model.addAttribute("cargo", service.obtener(id));
            model.addAttribute("motivoCapturado", motivo);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/cargo-detalle";
        }
        flash.addFlashAttribute("mensaje", "Cargo cancelado; el historial permanece disponible");
        return "redirect:/admin/catalogos/cargos";
    }

    @GetMapping("/generar")
    String generador(Model model) {
        prepararGenerador(model, new GeneracionCargosForm());
        return "admin/cargo-generar";
    }

    @PostMapping("/generar")
    String generar(@Valid @ModelAttribute("form") GeneracionCargosForm form,
                   BindingResult errores, Model model, RedirectAttributes flash) {
        validarGeneracion(form, errores);
        if (errores.hasErrors()) {
            prepararGenerador(model, form);
            return "admin/cargo-generar";
        }
        try {
            var resultado = service.generar(form.request());
            flash.addFlashAttribute("mensaje", "Generación terminada: "
                    + resultado.cargosGenerados() + " cargos nuevos, "
                    + resultado.cargosYaExistentes() + " ya existían; "
                    + resultado.cuotasRevisadas() + " cuotas revisadas");
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararGenerador(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/cargo-generar";
        }
        return "redirect:/admin/catalogos/cargos";
    }

    private void prepararManual(Model model, CargoForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        completarPredeterminados(form, instituciones, planteles);
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("inscripcionSeleccionada", etiquetaInscripcion(form.getInscripcionId()));
        model.addAttribute("conceptoSeleccionado", etiquetaConcepto(form.getConceptoCobroId()));
        model.addAttribute("periodoSeleccionado", etiquetaPeriodo(form.getPeriodoAcademicoId()));
    }

    private void prepararGenerador(Model model, GeneracionCargosForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
    }

    private void completarPredeterminados(CargoForm form, List<InstitucionResponse> instituciones,
                                           List<PlantelResponse> planteles) {
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
    }

    private void validarAlcance(CargoForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() != null) alcance.validarPlantel(form.getPlantelId());
        if (form.getInscripcionId() != null) alcance.validarRecurso(ModuloCatalogo.INSCRIPCIONES,
                form.getInscripcionId());
        if (form.getConceptoCobroId() != null) alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO,
                form.getConceptoCobroId());
        if (form.getPeriodoAcademicoId() != null) alcance.validarRecurso(ModuloCatalogo.PERIODOS,
                form.getPeriodoAcademicoId());
    }

    private void validarRelaciones(CargoForm form, BindingResult errores) {
        if (form.getInstitucionId() == null || form.getPlantelId() == null) return;
        if (form.getInscripcionId() != null) {
            var inscripcion = inscripcionService.obtener(form.getInscripcionId());
            if (!inscripcion.institucionId().equals(form.getInstitucionId())) {
                errores.rejectValue("inscripcionId", "cargo.inscripcion.institucion",
                        "La inscripción no pertenece a la institución indicada");
            }
            if (!inscripcion.plantelId().equals(form.getPlantelId())) {
                errores.rejectValue("inscripcionId", "cargo.inscripcion.plantel",
                        "La inscripción no pertenece al plantel indicado");
            }
        }
        if (form.getConceptoCobroId() != null
                && !conceptoService.obtener(form.getConceptoCobroId()).institucionId()
                .equals(form.getInstitucionId())) {
            errores.rejectValue("conceptoCobroId", "cargo.concepto.institucion",
                    "El concepto no pertenece a la institución indicada");
        }
    }

    private void validarGeneracion(GeneracionCargosForm form, BindingResult errores) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() == null) {
            if (form.getInstitucionId() != null) {
                alcance.validarAdministracionInstitucional(form.getInstitucionId());
            }
            return;
        }
        alcance.validarPlantel(form.getPlantelId());
        if (form.getInstitucionId() != null
                && !plantelService.obtener(form.getPlantelId()).institucionId()
                .equals(form.getInstitucionId())) {
            errores.rejectValue("plantelId", "cargo.generacion.plantel",
                    "El plantel no pertenece a la institución indicada");
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

    private String etiquetaPeriodo(Long id) {
        if (id == null) return "";
        var periodo = periodoService.obtener(id);
        return periodo.codigo() + " · " + periodo.nombre();
    }
}
