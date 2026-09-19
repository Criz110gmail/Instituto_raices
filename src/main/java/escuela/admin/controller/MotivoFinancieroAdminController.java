package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.MotivoFinancieroForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.MotivoFinancieroResponse;
import escuela.finanzas.entity.NaturalezaMotivoFinanciero;
import escuela.finanzas.service.MotivoFinancieroService;
import escuela.institucion.service.InstitucionService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/motivos-financieros")
public class MotivoFinancieroAdminController {
    private final MotivoFinancieroService service;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new MotivoFinancieroForm(), null);
        return "admin/motivo-financiero-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") MotivoFinancieroForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validar(form);
        if (errores.hasErrors()) { preparar(model, form, null); return "admin/motivo-financiero-form"; }
        try { service.crear(form.request()); }
        catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            error(model, form, null, ex); return "admin/motivo-financiero-form";
        }
        flash.addFlashAttribute("mensaje", "Motivo financiero creado correctamente");
        return "redirect:/admin/catalogos/motivos-financieros";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.MOTIVOS_FINANCIEROS, id);
        MotivoFinancieroResponse respuesta = service.obtener(id);
        alcance.validarAdministracionInstitucional(respuesta.institucionId());
        preparar(model, MotivoFinancieroForm.desde(respuesta), id);
        return "admin/motivo-financiero-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id, @Valid @ModelAttribute("form") MotivoFinancieroForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.MOTIVOS_FINANCIEROS, id); validar(form);
        if (errores.hasErrors()) { preparar(model, form, id); return "admin/motivo-financiero-form"; }
        try { service.actualizar(id, form.request()); }
        catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            error(model, form, id, ex); return "admin/motivo-financiero-form";
        }
        flash.addFlashAttribute("mensaje", "Motivo financiero actualizado correctamente");
        return "redirect:/admin/catalogos/motivos-financieros";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.MOTIVOS_FINANCIEROS, id);
        try { service.desactivar(id, version); }
        catch (ReglaNegocioException | DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            MotivoFinancieroResponse respuesta = service.obtener(id);
            error(model, MotivoFinancieroForm.desde(respuesta), id, ex);
            return "admin/motivo-financiero-form";
        }
        flash.addFlashAttribute("mensaje", "Motivo financiero desactivado correctamente");
        return "redirect:/admin/catalogos/motivos-financieros";
    }

    private void preparar(Model model, MotivoFinancieroForm form, Long id) {
        var instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        model.addAttribute("form", form); model.addAttribute("id", id);
        model.addAttribute("edicion", id != null); model.addAttribute("instituciones", instituciones);
        model.addAttribute("naturalezas", NaturalezaMotivoFinanciero.values());
        model.addAttribute("reservado", "COBROS_ESCOLARES".equalsIgnoreCase(form.getCodigo())
                || "TRASPASO_INTERNO".equalsIgnoreCase(form.getCodigo()));
    }

    private void validar(MotivoFinancieroForm form) {
        if (form.getInstitucionId() != null) alcance.validarAdministracionInstitucional(form.getInstitucionId());
    }

    private void error(Model model, MotivoFinancieroForm form, Long id, RuntimeException ex) {
        preparar(model, form, id); model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
    }
}
