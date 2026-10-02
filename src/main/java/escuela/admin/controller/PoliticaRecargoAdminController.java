package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.cobranza.entity.*;
import escuela.cobranza.service.*;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.response.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/politicas-recargo")
public class PoliticaRecargoAdminController {
    private final PoliticaRecargoService service;
    private final ConceptoCobroService conceptoService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new PoliticaRecargoForm(), null);
        return "admin/politica-recargo-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") PoliticaRecargoForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validar(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/politica-recargo-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            error(model, form, null, excepcion);
            return "admin/politica-recargo-form";
        }
        flash.addFlashAttribute("mensaje", "Política de recargo creada correctamente");
        return "redirect:/admin/catalogos/politicas-recargo";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.POLITICAS_RECARGO, id);
        preparar(model, PoliticaRecargoForm.desde(service.obtener(id)), id);
        return "admin/politica-recargo-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") PoliticaRecargoForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.POLITICAS_RECARGO, id);
        validar(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/politica-recargo-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            error(model, form, id, excepcion);
            return "admin/politica-recargo-form";
        }
        flash.addFlashAttribute("mensaje",
                "Política actualizada; los recargos emitidos conservaron sus valores históricos");
        return "redirect:/admin/catalogos/politicas-recargo";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.POLITICAS_RECARGO, id);
        service.desactivar(id, version);
        flash.addFlashAttribute("mensaje", "Política de recargo desactivada");
        return "redirect:/admin/catalogos/politicas-recargo";
    }

    @GetMapping("/generar")
    String generador(Model model) {
        prepararGenerador(model, new GeneracionRecargosForm());
        return "admin/recargo-generar";
    }

    @GetMapping("/generar/vista-previa")
    String vistaPrevia(@Valid @ModelAttribute("form") GeneracionRecargosForm form,
                       BindingResult errores,
                       @RequestParam(defaultValue = "0") int pagina,
                       @RequestParam(defaultValue = "25") int tamanio,
                       Model model) {
        validarGenerador(form, errores);
        prepararGenerador(model, form);
        if (!errores.hasErrors()) {
            model.addAttribute("vistaPrevia", service.previsualizar(form.request(), pagina, tamanio));
            model.addAttribute("tamanio", Math.min(Math.max(tamanio, 10), 100));
        }
        return "admin/recargo-generar";
    }

    @PostMapping("/generar")
    String generar(@Valid @ModelAttribute("form") GeneracionRecargosForm form,
                   BindingResult errores,
                   @RequestParam(defaultValue = "false") boolean confirmacion,
                   Model model, RedirectAttributes flash) {
        validarGenerador(form, errores);
        if (!confirmacion)
            errores.reject("recargo.confirmacion",
                    "Primero visualiza los recargos y confirma el resultado mostrado");
        if (errores.hasErrors()) {
            prepararGenerador(model, form);
            return "admin/recargo-generar";
        }
        try {
            var resultado = service.generar(form.request());
            flash.addFlashAttribute("mensaje", "Recargos: " + resultado.recargosGenerados()
                    + " nuevos, " + resultado.recargosExistentes() + " existentes; "
                    + resultado.cargosRevisados() + " cargos revisados");
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararGenerador(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/recargo-generar";
        }
        return "redirect:/admin/catalogos/ajustes-cargo";
    }

    private void preparar(Model model, PoliticaRecargoForm form, Long id) {
        var instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        if ((form.getMoneda() == null || form.getMoneda().isBlank())
                && form.getInstitucionId() != null)
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .map(InstitucionResponse::monedaPredeterminada).ifPresent(form::setMoneda);
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("modalidades", ModalidadBeca.values());
        model.addAttribute("periodicidades", PeriodicidadRecargo.values());
        model.addAttribute("tiposLimite", TipoLimiteRecargo.values());
        model.addAttribute("conceptoSeleccionado", etiqueta(form.getConceptoCobroId()));
    }

    private void validar(PoliticaRecargoForm form, BindingResult errores) {
        if (form.getInstitucionId() != null)
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        if (form.getConceptoCobroId() != null) {
            alcance.validarRecurso(ModuloCatalogo.CONCEPTOS_COBRO, form.getConceptoCobroId());
            if (form.getInstitucionId() != null && !conceptoService.obtener(form.getConceptoCobroId())
                    .institucionId().equals(form.getInstitucionId()))
                errores.rejectValue("conceptoCobroId", "politica.concepto",
                        "El concepto no pertenece a la institución indicada");
        }
    }

    private void prepararGenerador(Model model, GeneracionRecargosForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
    }

    private void validarGenerador(GeneracionRecargosForm form, BindingResult errores) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelId() == null) {
            if (form.getInstitucionId() != null)
                alcance.validarAdministracionInstitucional(form.getInstitucionId());
            return;
        }
        alcance.validarPlantel(form.getPlantelId());
        if (form.getInstitucionId() != null && !plantelService.obtener(form.getPlantelId())
                .institucionId().equals(form.getInstitucionId()))
            errores.rejectValue("plantelId", "recargo.plantel",
                    "El plantel no pertenece a la institución indicada");
    }

    private String etiqueta(Long id) {
        if (id == null) return "";
        var concepto = conceptoService.obtener(id);
        return concepto.codigo() + " · " + concepto.nombre();
    }

    private void error(Model model, PoliticaRecargoForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }
}
