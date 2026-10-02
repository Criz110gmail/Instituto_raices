package escuela.admin.controller;

import escuela.admin.dto.*;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.admin.service.JasperComprobantePagoService;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.ReglaNegocioException;
import escuela.cobranza.entity.SituacionCobro;
import escuela.cobranza.service.CargoService;
import escuela.cobranza.service.CobranzaInscripcionService;
import escuela.finanzas.dto.response.PagoResponse;
import escuela.finanzas.entity.MetodoPago;
import escuela.finanzas.service.DevolucionPagoService;
import escuela.finanzas.service.CancelacionPagoService;
import escuela.finanzas.service.PagoService;
import escuela.finanzas.service.ValidacionPagoService;
import escuela.finanzas.dto.request.ValidacionPagoRequest;
import escuela.finanzas.dto.request.RechazoPagoRequest;
import escuela.finanzas.dto.request.CancelacionPagoRequest;
import escuela.institucion.dto.response.*;
import escuela.institucion.service.*;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static escuela.common.support.FormatoMoneda.formatear;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.net.URLEncoder;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pagos")
public class PagoAdminController {
    private final PagoService service;
    private final ValidacionPagoService validacionService;
    private final DevolucionPagoService devolucionService;
    private final CancelacionPagoService cancelacionService;
    private final CargoService cargoService;
    private final CobranzaInscripcionService cobranzaInscripcionService;
    private final InstitucionService institucionService;
    private final PlantelService plantelService;
    private final AlcanceDatosService alcance;
    private final JasperComprobantePagoService jasperComprobante;

    @GetMapping("/nuevo")
    String nuevo(@RequestParam(required = false) Long cargoId,
                 @RequestParam(required = false) Long retornoInscripcionId,
                 Model model) {
        PagoForm form = new PagoForm();
        if (cargoId != null) prepararDesdeCargo(form, cargoId, retornoInscripcionId, model);
        preparar(model, form);
        return "admin/pago-form";
    }

    String nuevo(Model model) {
        return nuevo(null, null, model);
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
                   @RequestParam(required = false) String motivoCambioCuenta,
                   @RequestParam Long version, Authentication authentication,
                   Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        try {
            PagoResponse pago = validacionService.validar(id,
                    new ValidacionPagoRequest(cuentaDestinoId, motivoCambioCuenta, version));
            flash.addFlashAttribute("mensaje", "Pago " + pago.folio()
                    + " validado; el ingreso y sus aplicaciones quedaron publicados");
            return "redirect:/admin/pagos/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararDetalle(model, service.obtener(id), authentication);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            model.addAttribute("motivoCambioCuentaCapturado", motivoCambioCuenta);
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
            flash.addFlashAttribute("mensaje", "Devolución por " + formatear(devolucion.monto())
                    + " ejecutada y publicada como egreso");
            return "redirect:/admin/pagos/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararDetalle(model, service.obtener(id), authentication, form);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            return "admin/pago-detalle";
        }
    }

    @PostMapping("/{id}/cancelar")
    String cancelar(@PathVariable Long id, @RequestParam Long version,
                    @RequestParam(required = false) String motivo, Authentication authentication,
                    Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        try {
            PagoResponse pago = cancelacionService.cancelar(id, new CancelacionPagoRequest(version, motivo));
            flash.addFlashAttribute("mensaje", "Pago " + pago.folio()
                    + " cancelado; el historial y las compensaciones quedaron registrados");
            return "redirect:/admin/pagos/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException
                 | ObjectOptimisticLockingFailureException excepcion) {
            prepararDetalle(model, service.obtener(id), authentication);
            model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
            model.addAttribute("motivoCancelacionCapturado", motivo);
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
        boolean accesoRecuperacion = authentication != null
                && authentication.getPrincipal() instanceof UsuarioPrincipal principal
                && principal.accesoRecuperacion();
        boolean tienePermisoValidar = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_VALIDAR"));
        model.addAttribute("accesoRecuperacion", accesoRecuperacion);
        model.addAttribute("puedeValidar", tienePermisoValidar && !accesoRecuperacion);
        boolean puedeDevolver = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_DEVOLVER"));
        model.addAttribute("puedeDevolver", puedeDevolver);
        model.addAttribute("puedeCancelar", authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PAGO_CANCELAR")));
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
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    @GetMapping("/{id}/comprobante-pago")
    void comprobantePago(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response)
            throws java.io.IOException {
        alcance.validarRecurso(ModuloCatalogo.PAGOS, id);
        PagoResponse pago = service.obtener(id);
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" +
                URLEncoder.encode("comprobante-" + pago.folio() + ".pdf", StandardCharsets.UTF_8));
        jasperComprobante.exportar(pago, response.getOutputStream());
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
        model.addAttribute("pagoRapido", form.getRetornoInscripcionId() != null);
    }

    private void validarAlcance(PagoForm form) {
        if (form.getInstitucionId() != null) alcance.validarInstitucion(form.getInstitucionId());
        if (form.getPlantelRegistroId() != null) alcance.validarPlantel(form.getPlantelRegistroId());
        if (form.getTutorId() != null) alcance.validarRecurso(ModuloCatalogo.TUTORES, form.getTutorId());
        if (form.getSolicitudes() != null) form.getSolicitudes().stream()
                .filter(s -> s.getCargoId() != null)
                .forEach(s -> alcance.validarRecurso(ModuloCatalogo.CARGOS, s.getCargoId()));
    }

    private void prepararDesdeCargo(PagoForm form, Long cargoId, Long retornoInscripcionId,
                                    Model model) {
        alcance.validarRecurso(ModuloCatalogo.CARGOS, cargoId);
        var cargo = cargoService.obtener(cargoId);
        if (cargo.estadoRegistro() != escuela.cobranza.entity.EstadoRegistroCargo.EMITIDO
                || cargo.situacionCobro() == SituacionCobro.PAGADO
                || cargo.situacionCobro() == SituacionCobro.CANCELADO) {
            throw new ReglaNegocioException("Este pago por cobrar ya no tiene saldo disponible");
        }
        form.setInstitucionId(cargo.institucionId());
        form.setPlantelRegistroId(cargo.plantelId());
        form.setMonto(cargo.saldoPendiente());
        form.setMoneda(cargo.moneda());
        form.setMetodo(MetodoPago.EFECTIVO);
        form.setRetornoInscripcionId(retornoInscripcionId == null
                ? cargo.inscripcionId() : retornoInscripcionId);
        SolicitudAplicacionPagoForm solicitud = new SolicitudAplicacionPagoForm();
        solicitud.setCargoId(cargo.id());
        solicitud.setCargoEtiqueta(cargo.alumnoMatricula() + " · " + cargo.alumnoNombre()
                + " · " + cargo.conceptoNombre());
        solicitud.setSaldoReferencia(cargo.saldoPendiente());
        solicitud.setMontoSolicitado(cargo.saldoPendiente());
        form.getSolicitudes().add(solicitud);
        var tutor = cobranzaInscripcionService.tutorParaCargo(cargoId);
        if (tutor == null) {
            model.addAttribute("advertenciaPagoRapido",
                    "El alumno no tiene un tutor responsable financiero vigente. Vincúlalo antes de registrar el pago.");
        } else {
            form.setTutorId(tutor.id());
            form.setTutorEtiqueta(tutor.etiqueta());
            if (tutor.responsablesDisponibles() > 1) {
                model.addAttribute("advertenciaPagoRapido",
                        "Se seleccionó el responsable financiero principal de la inscripción. Si no corresponde, corrige primero el vínculo del alumno.");
            }
        }
        model.addAttribute("cargoPrecargado", cargo);
    }
}
