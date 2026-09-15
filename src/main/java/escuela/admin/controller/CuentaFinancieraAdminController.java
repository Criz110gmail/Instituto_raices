package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.CuentaFinancieraResponse;
import escuela.finanzas.entity.TipoCuentaFinanciera;
import escuela.finanzas.service.CuentaFinancieraService;
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
@RequestMapping("/admin/cuentas-financieras")
public class CuentaFinancieraAdminController {

    private final CuentaFinancieraService service;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new CuentaFinancieraForm(), null);
        return "admin/cuenta-financiera-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") CuentaFinancieraForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        validarAlcance(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/cuenta-financiera-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException ex) {
            error(model, form, null, ex);
            return "admin/cuenta-financiera-form";
        }
        flash.addFlashAttribute("mensaje", "Cuenta financiera creada correctamente");
        return "redirect:/admin/catalogos/cuentas-financieras";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.CUENTAS_FINANCIERAS, id);
        preparar(model, CuentaFinancieraForm.desde(service.obtener(id)), id);
        return "admin/cuenta-financiera-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") CuentaFinancieraForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CUENTAS_FINANCIERAS, id);
        validarAlcance(form, errores);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/cuenta-financiera-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException ex) {
            error(model, form, id, ex);
            return "admin/cuenta-financiera-form";
        }
        flash.addFlashAttribute("mensaje", "Cuenta financiera actualizada correctamente");
        return "redirect:/admin/catalogos/cuentas-financieras";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.CUENTAS_FINANCIERAS, id);
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException ex) {
            CuentaFinancieraResponse respuesta = service.obtener(id);
            error(model, CuentaFinancieraForm.desde(respuesta), id, ex);
            return "admin/cuenta-financiera-form";
        }
        flash.addFlashAttribute("mensaje", "Cuenta financiera desactivada correctamente");
        return "redirect:/admin/catalogos/cuentas-financieras";
    }

    private void preparar(Model model, CuentaFinancieraForm form, Long id) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
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
        model.addAttribute("tipos", TipoCuentaFinanciera.values());
    }

    private void validarAlcance(CuentaFinancieraForm form, BindingResult errores) {
        if (form.getInstitucionId() == null) return;
        if (form.getPlantelId() == null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
            return;
        }
        alcance.validarPlantel(form.getPlantelId());
        PlantelResponse plantel = plantelService.obtener(form.getPlantelId());
        if (!plantel.institucionId().equals(form.getInstitucionId())) {
            errores.rejectValue("plantelId", "cuenta.plantel",
                    "El plantel no pertenece a la institución indicada");
        }
    }

    private void error(Model model, CuentaFinancieraForm form, Long id, RuntimeException ex) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
    }
}
