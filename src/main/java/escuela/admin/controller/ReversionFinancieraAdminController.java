package escuela.admin.controller;

import escuela.admin.dto.ReversionFinancieraForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.ObjetivoReversionResponse;
import escuela.finanzas.entity.TipoReversionFinanciera;
import escuela.finanzas.service.ReversionFinancieraService;
import escuela.institucion.service.InstitucionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/reversiones")
public class ReversionFinancieraAdminController {
    private final ReversionFinancieraService service;
    private final InstitucionService institucionService;

    @GetMapping("/movimientos/{id}/nueva")
    String nuevaMovimiento(@PathVariable Long id, Model model) {
        ObjetivoReversionResponse objetivo = service.obtenerMovimiento(id);
        preparar(model, objetivo, formulario(objetivo));
        return "admin/reversion-financiera-form";
    }

    @PostMapping("/movimientos/{id}")
    String revertirMovimiento(@PathVariable Long id,
                              @Valid @ModelAttribute("form") ReversionFinancieraForm form,
                              BindingResult errores, Model model, RedirectAttributes flash) {
        ObjetivoReversionResponse objetivo = service.obtenerMovimiento(id);
        if (errores.hasErrors()) {
            preparar(model, objetivo, form);
            return "admin/reversion-financiera-form";
        }
        try {
            service.revertirMovimiento(id, form.request());
            flash.addFlashAttribute("mensaje", "La operación fue revertida con un movimiento compensatorio");
            return redireccion(objetivo.institucionId());
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            preparar(model, service.obtenerMovimiento(id), form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/reversion-financiera-form";
        }
    }

    @GetMapping("/transferencias/{id}/nueva")
    String nuevaTransferencia(@PathVariable Long id, Model model) {
        ObjetivoReversionResponse objetivo = service.obtenerTransferencia(id);
        preparar(model, objetivo, formulario(objetivo));
        return "admin/reversion-financiera-form";
    }

    @PostMapping("/transferencias/{id}")
    String revertirTransferencia(@PathVariable Long id,
                                 @Valid @ModelAttribute("form") ReversionFinancieraForm form,
                                 BindingResult errores, Model model, RedirectAttributes flash) {
        ObjetivoReversionResponse objetivo = service.obtenerTransferencia(id);
        if (errores.hasErrors()) {
            preparar(model, objetivo, form);
            return "admin/reversion-financiera-form";
        }
        try {
            service.revertirTransferencia(id, form.request());
            flash.addFlashAttribute("mensaje", "La transferencia fue revertida de forma atómica en ambas cuentas");
            return redireccion(objetivo.institucionId());
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            preparar(model, service.obtenerTransferencia(id), form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/reversion-financiera-form";
        }
    }

    private ReversionFinancieraForm formulario(ObjetivoReversionResponse objetivo) {
        String zona = institucionService.obtener(objetivo.institucionId()).zonaHoraria();
        ReversionFinancieraForm form = new ReversionFinancieraForm();
        form.setFecha(LocalDateTime.now(ZoneId.of(zona)).withSecond(0).withNano(0));
        form.setVersion(objetivo.version());
        return form;
    }

    private void preparar(Model model, ObjetivoReversionResponse objetivo, ReversionFinancieraForm form) {
        String zona = institucionService.obtener(objetivo.institucionId()).zonaHoraria();
        model.addAttribute("objetivo", objetivo);
        model.addAttribute("form", form);
        model.addAttribute("esTransferencia", objetivo.tipo() == TipoReversionFinanciera.TRANSFERENCIA);
        model.addAttribute("fechaOriginalLocal", DateTimeFormatter
                .ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"))
                .withZone(ZoneId.of(zona)).format(objetivo.fechaOriginal()));
    }

    private String redireccion(Long institucionId) {
        return "redirect:/admin/movimientos-financieros?institucionId=" + institucionId;
    }
}
