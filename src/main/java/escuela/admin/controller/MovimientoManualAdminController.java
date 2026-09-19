package escuela.admin.controller;

import escuela.admin.dto.MovimientoManualForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.entity.DireccionMovimiento;
import escuela.finanzas.service.MotivoFinancieroService;
import escuela.finanzas.service.MovimientoManualService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.institucion.service.PlantelService;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/movimientos-financieros")
public class MovimientoManualAdminController {
    private final MovimientoManualService service;
    private final MotivoFinancieroService motivoService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        MovimientoManualForm form = new MovimientoManualForm();
        form.setDireccion(DireccionMovimiento.EGRESO);
        preparar(model, form);
        return "admin/movimiento-manual-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") MovimientoManualForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        if (errores.hasErrors()) { preparar(model, form); return "admin/movimiento-manual-form"; }
        try { service.registrar(form.request()); }
        catch (ReglaNegocioException | DataIntegrityViolationException ex) {
            preparar(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            return "admin/movimiento-manual-form";
        }
        flash.addFlashAttribute("mensaje", "Movimiento financiero registrado correctamente");
        return "redirect:/admin/movimientos-financieros?institucionId=" + form.getInstitucionId()
                + "&cuentaId=" + form.getCuentaId() + "&cuentaTexto="
                + java.net.URLEncoder.encode(form.getCuentaTexto() == null ? "" : form.getCuentaTexto(),
                java.nio.charset.StandardCharsets.UTF_8);
    }

    private void preparar(Model model, MovimientoManualForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        if (form.getFechaOperacion() == null && form.getInstitucionId() != null) {
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .ifPresent(i -> form.setFechaOperacion(LocalDateTime.now(java.time.ZoneId.of(i.zonaHoraria()))
                            .withSecond(0).withNano(0)));
        }
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", alcance.filtrarPlanteles(plantelService.listar()));
        model.addAttribute("direcciones", DireccionMovimiento.values());
        model.addAttribute("motivos", form.getInstitucionId() == null ? List.of()
                : motivoService.listarActivos(form.getInstitucionId()));
    }
}
