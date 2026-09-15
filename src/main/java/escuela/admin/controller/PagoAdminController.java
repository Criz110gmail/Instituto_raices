package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.MetodoPago;
import escuela.finanzas.service.PagoService;
import escuela.institucion.dto.response.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pagos")
public class PagoAdminController {
    private final PagoService service;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        PagoForm form = new PagoForm();
        form.setFolio("PAG-" + LocalDate.now().toString().replace("-", "") + "-"
                + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT));
        preparar(model, form);
        return "admin/pago-form";
    }

    @PostMapping
    String registrar(@Valid @ModelAttribute("form") PagoForm form, BindingResult errores,
                     @RequestParam(name = "comprobantes", required = false) List<MultipartFile> comprobantes,
                     Model model, RedirectAttributes flash) {
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form);
            return "admin/pago-form";
        }
        try {
            InstitucionResponse institucion = institucionService.obtener(form.getInstitucionId());
            PagoResponse pago = service.registrar(form.request(institucion.zonaHoraria()), comprobantes);
            flash.addFlashAttribute("mensaje", "Pago " + pago.folio()
                    + " registrado y pendiente de validación");
            return "redirect:/admin/pagos/" + pago.id() + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            preparar(model, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/pago-form";
        }
    }

    @GetMapping("/{id}/editar")
    String detalle(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        PagoResponse pago = service.obtener(id);
        model.addAttribute("pago", pago);
        String zona = institucionService.obtener(pago.institucionId()).zonaHoraria();
        model.addAttribute("fechaPagoLocal", DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"))
                .withZone(java.time.ZoneId.of(zona)).format(pago.fechaPago()));
        return "admin/pago-detalle";
    }

    @GetMapping("/{pagoId}/comprobantes/{comprobanteId}")
    ResponseEntity<Resource> descargar(@PathVariable Long pagoId, @PathVariable Long comprobanteId) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, pagoId);
        ArchivoDescarga descarga = service.descargarComprobante(pagoId, comprobanteId);
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    private void preparar(Model model, PagoForm form) {
        List<InstitucionResponse> instituciones = alcance.filtrarInstituciones(institucionService.listar());
        List<PlantelResponse> planteles = alcance.filtrarPlanteles(plantelService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        if (form.getPlantelRegistroId() == null && planteles.size() == 1) {
            form.setPlantelRegistroId(planteles.getFirst().id());
        }
        if ((form.getMoneda() == null || form.getMoneda().isBlank()) && form.getInstitucionId() != null) {
            instituciones.stream().filter(i -> i.id().equals(form.getInstitucionId())).findFirst()
                    .map(InstitucionResponse::monedaPredeterminada).ifPresent(form::setMoneda);
        }
        model.addAttribute("form", form);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("planteles", planteles);
        model.addAttribute("metodos", MetodoPago.values());
    }

    private void validarAlcance(PagoForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelRegistroId() != null) alcance.validarPlantel(form.getPlantelRegistroId());
        if (form.getTutorId() != null) alcance.validarRecurso(ModuloCatalogo.TUTORES, form.getTutorId());
        if (form.getSolicitudes() != null) form.getSolicitudes().stream()
                .filter(s -> s.getCargoId() != null)
                .forEach(s -> alcance.validarRecurso(ModuloCatalogo.CARGOS, s.getCargoId()));
    }
}
