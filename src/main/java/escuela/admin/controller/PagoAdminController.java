package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.ReglaNegocioException;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.MetodoPago;
import escuela.finanzas.service.DevolucionPagoService;
import escuela.finanzas.service.PagoService;
import escuela.finanzas.service.ValidacionPagoService;
import escuela.finanzas.dto.request.ValidacionPagoRequest;
import escuela.finanzas.dto.request.RechazoPagoRequest;
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
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pagos")
public class PagoAdminController {
    private final PagoService service;
    private final ValidacionPagoService validacionService;
    private final DevolucionPagoService devolucionService;
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
    String detalle(@PathVariable Long id, Authentication authentication, Model model) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        prepararDetalle(model, service.obtener(id), authentication);
        return "admin/pago-detalle";
    }

    @PostMapping("/{id}/validar")
    String validar(@PathVariable Long id, @RequestParam(required = false) Long cuentaDestinoId,
                   @RequestParam Long version, Authentication authentication,
                   Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        try {
            PagoResponse pago = validacionService.validar(id,
                    new ValidacionPagoRequest(cuentaDestinoId, version));
            flash.addFlashAttribute("mensaje", "Pago " + pago.folio()
                    + " validado; el ingreso y sus aplicaciones quedaron publicados");
            return "redirect:/admin/pagos/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararDetalle(model, service.obtener(id), authentication);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/pago-detalle";
        }
    }

    @PostMapping("/{id}/rechazar")
    String rechazar(@PathVariable Long id, @RequestParam(required = false) String motivo,
                    @RequestParam Long version, Authentication authentication,
                    Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        try {
            PagoResponse pago = validacionService.rechazar(id, new RechazoPagoRequest(motivo, version));
            flash.addFlashAttribute("mensaje", "Pago " + pago.folio() + " rechazado sin afectar saldos");
            return "redirect:/admin/pagos/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararDetalle(model, service.obtener(id), authentication);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            model.addAttribute("motivoCapturado", motivo);
            return "admin/pago-detalle";
        }
    }

    @PostMapping("/{id}/devolver")
    String devolver(@PathVariable Long id,
                    @Valid @ModelAttribute("devolucionForm") DevolucionPagoForm form,
                    BindingResult errores, Authentication authentication,
                    Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        if (errores.hasErrors()) {
            prepararDetalle(model, service.obtener(id), authentication, form);
            return "admin/pago-detalle";
        }
        try {
            var devolucion = devolucionService.ejecutar(form.request(id));
            flash.addFlashAttribute("mensaje", "Devolución por " + devolucion.monto().toPlainString()
                    + " " + devolucion.moneda() + " ejecutada y publicada como egreso");
            return "redirect:/admin/pagos/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararDetalle(model, service.obtener(id), authentication, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/pago-detalle";
        }
    }

    private void prepararDetalle(Model model, PagoResponse pago, Authentication authentication) {
        prepararDetalle(model, pago, authentication, null);
    }

    private void prepararDetalle(Model model, PagoResponse pago, Authentication authentication,
                                  DevolucionPagoForm formCapturado) {
        model.addAttribute("pago", pago);
        String zona = institucionService.obtener(pago.institucionId()).zonaHoraria();
        model.addAttribute("fechaPagoLocal", DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"))
                .withZone(java.time.ZoneId.of(zona)).format(pago.fechaPago()));
        if (pago.validadoEn() != null) {
            model.addAttribute("fechaValidacionLocal", DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"))
                    .withZone(java.time.ZoneId.of(zona)).format(pago.validadoEn()));
        }
        model.addAttribute("puedeValidar", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_VALIDAR")));
        boolean puedeDevolver = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_DEVOLVER"));
        model.addAttribute("puedeDevolver", puedeDevolver);
        if (pago.estado() == escuela.finanzas.entity.EstadoPago.VALIDADO) {
            var resumen = devolucionService.resumen(pago.id());
            model.addAttribute("resumenDevolucion", resumen);
            Map<Long, String> fechasDevolucion = new LinkedHashMap<>();
            var formato = DateTimeFormatter.ofPattern("dd MMM yyyy · HH:mm", new Locale("es", "MX"))
                    .withZone(java.time.ZoneId.of(zona));
            resumen.devoluciones().forEach(d -> fechasDevolucion.put(d.id(), formato.format(d.fecha())));
            model.addAttribute("fechasDevolucion", fechasDevolucion);
            DevolucionPagoForm form = formCapturado == null ? formularioDevolucion(pago, zona) : formCapturado;
            model.addAttribute("devolucionForm", form);
        }
    }

    private DevolucionPagoForm formularioDevolucion(PagoResponse pago, String zona) {
        DevolucionPagoForm form = new DevolucionPagoForm();
        form.setCuentaOrigenId(pago.cuentaDestinoId());
        form.setCuentaOrigenTexto(pago.cuentaDestinoNombre());
        form.setFecha(java.time.LocalDateTime.now(java.time.ZoneId.of(zona)).withSecond(0).withNano(0));
        form.setBeneficiario(pago.nombrePagador() == null || pago.nombrePagador().isBlank()
                ? pago.tutorNombre() : pago.nombrePagador());
        form.setPagoVersion(pago.auditoria().version());
        return form;
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
