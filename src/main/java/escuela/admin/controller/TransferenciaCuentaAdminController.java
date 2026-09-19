package escuela.admin.controller;

import escuela.admin.dto.TransferenciaCuentaForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.service.TransferenciaCuentaService;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
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
import java.time.ZoneId;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/transferencias")
public class TransferenciaCuentaAdminController {
    private final TransferenciaCuentaService service;
    private final InstitucionService institucionService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nueva")
    String nueva(Model model) {
        TransferenciaCuentaForm form = new TransferenciaCuentaForm();
        preparar(model, form);
        return "admin/transferencia-cuenta-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") TransferenciaCuentaForm form,
                 BindingResult errores, Model model, RedirectAttributes flash) {
        if (form.getCuentaOrigenId() != null && form.getCuentaOrigenId().equals(form.getCuentaDestinoId()))
            errores.rejectValue("cuentaDestinoId", "transferencia.cuentas", "Selecciona una cuenta de destino diferente");
        if (errores.hasErrors()) { preparar(model, form); return "admin/transferencia-cuenta-form"; }
        try { service.transferir(form.request()); }
        catch (ReglaNegocioException | DataIntegrityViolationException ex) {
            preparar(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(ex));
            return "admin/transferencia-cuenta-form";
        }
        flash.addFlashAttribute("mensaje", "Transferencia aplicada correctamente en ambas cuentas");
        return "redirect:/admin/movimientos-financieros?institucionId=" + form.getInstitucionId()
                + "&cuentaId=" + form.getCuentaOrigenId() + "&cuentaTexto="
                + java.net.URLEncoder.encode(form.getCuentaOrigenTexto() == null ? "" : form.getCuentaOrigenTexto(),
                java.nio.charset.StandardCharsets.UTF_8);
    }

    private void preparar(Model model, TransferenciaCuentaForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1)
            form.setInstitucionId(instituciones.getFirst().id());
        if (form.getFecha() == null && form.getInstitucionId() != null)
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .ifPresent(i -> form.setFecha(LocalDateTime.now(ZoneId.of(i.zonaHoraria()))
                            .withSecond(0).withNano(0)));
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
    }
}
