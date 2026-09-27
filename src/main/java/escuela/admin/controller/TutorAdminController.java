package escuela.admin.controller;

import escuela.admin.dto.ModuloCatalogo;
import escuela.admin.dto.TutorForm;
import escuela.admin.dto.PortalTutorCuentaForm;
import escuela.admin.support.MensajeErrorFormulario;
import escuela.archivo.dto.ArchivoDescarga;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.dto.response.InstitucionResponse;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.dto.response.InvitacionEmitidaResponse;
import escuela.seguridad.dto.response.RecuperacionPasswordEmitidaResponse;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.InvitacionUsuarioService;
import escuela.seguridad.service.RecuperacionPasswordService;
import escuela.tutor.dto.response.TutorResponse;
import escuela.tutor.dto.response.IdentificacionTutorResponse;
import escuela.tutor.entity.TipoIdentificacionTutor;
import escuela.tutor.service.IdentificacionTutorService;
import escuela.tutor.service.TutorService;
import escuela.tutor.service.AccesoPortalTutorService;
import escuela.tutor.dto.response.PortalTutorCuentaResponse;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/tutores")
public class TutorAdminController {
    private static final Pattern RUTA_IDENTIFICACION =
            Pattern.compile("/admin/tutores/(\\d+)/identificacion$");

    private final TutorService service;
    private final IdentificacionTutorService identificacionService;
    private final InstitucionService institucionService;
    private final AccesoPortalTutorService accesoPortalService;
    private final InvitacionUsuarioService invitacionService;
    private final RecuperacionPasswordService recuperacionPasswordService;
    private final AlcanceDatosService alcance;

    @GetMapping("/nuevo")
    String nuevo(Model model) {
        preparar(model, new TutorForm(), null);
        return "admin/tutor-form";
    }

    @PostMapping
    String crear(@Valid @ModelAttribute("form") TutorForm form, BindingResult errores,
                 Model model, RedirectAttributes flash) {
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, null);
            return "admin/tutor-form";
        }
        try {
            service.crear(form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, null, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Tutor registrado correctamente");
        return "redirect:/admin/catalogos/tutores";
    }

    @GetMapping("/{id}/editar")
    String editar(@PathVariable Long id, Model model) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        TutorResponse tutor = service.obtener(id);
        alcance.validarAdministracionInstitucional(tutor.institucionId());
        preparar(model, TutorForm.desde(tutor), id);
        return "admin/tutor-form";
    }

    @PostMapping("/{id}")
    String actualizar(@PathVariable Long id,
                      @Valid @ModelAttribute("form") TutorForm form,
                      BindingResult errores, Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        validarAlcance(form);
        if (errores.hasErrors()) {
            preparar(model, form, id);
            return "admin/tutor-form";
        }
        try {
            service.actualizar(id, form.request());
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, form, id, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Tutor actualizado correctamente");
        return "redirect:/admin/catalogos/tutores";
    }

    @PostMapping("/{id}/desactivar")
    String desactivar(@PathVariable Long id, @RequestParam Long version,
                      Model model, RedirectAttributes flash) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        TutorResponse tutor = service.obtener(id);
        alcance.validarAdministracionInstitucional(tutor.institucionId());
        try {
            service.desactivar(id, version);
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            prepararError(model, TutorForm.desde(tutor), id, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Tutor desactivado correctamente");
        return "redirect:/admin/catalogos/tutores";
    }

    @PostMapping("/{id}/cuenta-portal")
    String crearCuentaPortal(@PathVariable Long id,
                             @Valid @ModelAttribute("portalForm") PortalTutorCuentaForm portalForm,
                             BindingResult errores, Model model, RedirectAttributes flash) {
        TutorResponse tutor = tutorAdministrable(id);
        if (errores.hasErrors()) {
            preparar(model, TutorForm.desde(tutor), id);
            model.addAttribute("portalForm", portalForm);
            return "admin/tutor-form";
        }
        try {
            PortalTutorCuentaResponse cuenta = accesoPortalService.crear(id, portalForm.request());
            InvitacionEmitidaResponse invitacion = invitacionService.emitir(
                    cuenta.usuarioId(), Duration.ofHours(48));
            flash.addFlashAttribute("invitacionEnlace", enlaceActivacion(invitacion.token()));
            flash.addFlashAttribute("invitacionExpira", invitacion.expiraEn());
            flash.addFlashAttribute("mensaje", "Acceso al portal creado. Copia el enlace de activación antes de salir");
            return "redirect:/admin/tutores/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            preparar(model, TutorForm.desde(tutor), id);
            model.addAttribute("portalForm", portalForm);
            model.addAttribute("errorCuentaPortal", MensajeErrorFormulario.desde(excepcion));
            return "admin/tutor-form";
        }
    }

    @PostMapping("/{id}/cuenta-portal/invitacion")
    String emitirInvitacionPortal(@PathVariable Long id, Model model, RedirectAttributes flash) {
        TutorResponse tutor = tutorAdministrable(id);
        try {
            PortalTutorCuentaResponse cuenta = accesoPortalService.obtener(id)
                    .orElseThrow(() -> new ReglaNegocioException("El tutor no tiene una cuenta de portal"));
            InvitacionEmitidaResponse invitacion = invitacionService.emitir(
                    cuenta.usuarioId(), Duration.ofHours(48));
            flash.addFlashAttribute("invitacionEnlace", enlaceActivacion(invitacion.token()));
            flash.addFlashAttribute("invitacionExpira", invitacion.expiraEn());
            flash.addFlashAttribute("mensaje", "Enlace de activación generado. Cópialo antes de salir");
            return "redirect:/admin/tutores/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            return errorCuentaPortal(model, tutor, id, excepcion);
        }
    }

    @PostMapping("/{id}/cuenta-portal/recuperacion")
    String emitirRecuperacionPortal(@PathVariable Long id, Model model, RedirectAttributes flash) {
        TutorResponse tutor = tutorAdministrable(id);
        try {
            PortalTutorCuentaResponse cuenta = accesoPortalService.obtener(id)
                    .orElseThrow(() -> new ReglaNegocioException("El tutor no tiene una cuenta de portal"));
            RecuperacionPasswordEmitidaResponse recuperacion = recuperacionPasswordService.emitir(
                    cuenta.usuarioId(), Duration.ofMinutes(30));
            String enlace = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/restablecer-password").queryParam("token", recuperacion.token())
                    .build().toUriString();
            flash.addFlashAttribute("recuperacionEnlace", enlace);
            flash.addFlashAttribute("recuperacionExpira", recuperacion.expiraEn());
            flash.addFlashAttribute("mensaje", "Enlace para cambiar contraseña generado. Cópialo antes de salir");
            return "redirect:/admin/tutores/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            return errorCuentaPortal(model, tutor, id, excepcion);
        }
    }

    @PostMapping("/{id}/cuenta-portal/estado")
    String cambiarEstadoCuentaPortal(@PathVariable Long id, @RequestParam Long version,
                                     @RequestParam boolean activar, Model model,
                                     RedirectAttributes flash) {
        TutorResponse tutor = tutorAdministrable(id);
        try {
            PortalTutorCuentaResponse cuenta = accesoPortalService.cambiarDisponibilidad(id, version, activar);
            flash.addFlashAttribute("mensaje", activar
                    ? (cuenta.credencialConfigurada() ? "Acceso al portal reactivado" : "Acceso habilitado; genera un enlace de activación")
                    : "Acceso al portal desactivado correctamente");
            return "redirect:/admin/tutores/" + id + "/editar";
        } catch (ReglaNegocioException | DataIntegrityViolationException |
                 ObjectOptimisticLockingFailureException excepcion) {
            return errorCuentaPortal(model, tutor, id, excepcion);
        }
    }

    @PostMapping("/{id}/identificacion")
    String asignarIdentificacion(@PathVariable Long id,
                                 @RequestParam TipoIdentificacionTutor tipo,
                                 @RequestParam("archivo") MultipartFile archivo,
                                 Model model, RedirectAttributes flash) {
        TutorResponse tutor = tutorAdministrable(id);
        try {
            identificacionService.asignar(id, tipo, archivo);
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararErrorIdentificacion(model, tutor, id, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "Identificación oficial actualizada correctamente");
        return "redirect:/admin/tutores/" + id + "/editar";
    }

    @PostMapping("/{id}/identificacion/retirar")
    String retirarIdentificacion(@PathVariable Long id, Model model, RedirectAttributes flash) {
        TutorResponse tutor = tutorAdministrable(id);
        try {
            identificacionService.retirar(id);
        } catch (ReglaNegocioException | DataIntegrityViolationException excepcion) {
            prepararErrorIdentificacion(model, tutor, id, excepcion);
            return "admin/tutor-form";
        }
        flash.addFlashAttribute("mensaje", "La identificación se retiró del expediente vigente");
        return "redirect:/admin/tutores/" + id + "/editar";
    }

    @GetMapping("/{tutorId}/identificaciones/{identificacionId}")
    ResponseEntity<Resource> descargarIdentificacion(@PathVariable Long tutorId,
                                                      @PathVariable Long identificacionId) {
        tutorAdministrable(tutorId);
        ArchivoDescarga descarga = identificacionService.descargar(tutorId, identificacionId);
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .contentType(MediaType.parseMediaType(descarga.tipoMime()))
                .contentLength(descarga.tamanoBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(descarga.nombreOriginal(), StandardCharsets.UTF_8).build().toString())
                .body(descarga.recurso());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    String identificacionDemasiadoGrande(HttpServletRequest request, Model model) {
        Matcher coincidencia = RUTA_IDENTIFICACION.matcher(request.getRequestURI());
        if (!coincidencia.matches()) {
            throw new ReglaNegocioException("El archivo supera el tamaño permitido");
        }
        Long id = Long.valueOf(coincidencia.group(1));
        TutorResponse tutor = tutorAdministrable(id);
        prepararErrorIdentificacion(model, tutor, id,
                new ReglaNegocioException("La identificación no puede superar 10 MB"));
        return "admin/tutor-form";
    }

    private void validarAlcance(TutorForm form) {
        if (form.getInstitucionId() != null) {
            alcance.validarAdministracionInstitucional(form.getInstitucionId());
        }
    }

    private void preparar(Model model, TutorForm form, Long id) {
        List<InstitucionResponse> instituciones =
                alcance.filtrarInstituciones(institucionService.listar());
        if (form.getInstitucionId() == null && instituciones.size() == 1) {
            form.setInstitucionId(instituciones.getFirst().id());
        }
        model.addAttribute("form", form);
        model.addAttribute("id", id);
        model.addAttribute("edicion", id != null);
        model.addAttribute("instituciones", instituciones);
        model.addAttribute("institucionSeleccionada", instituciones.stream()
                .filter(institucion -> institucion.id().equals(form.getInstitucionId()))
                .map(institucion -> institucion.codigo() + " · " + institucion.nombre())
                .findFirst().orElse("Institución no disponible"));
        model.addAttribute("tiposIdentificacion", TipoIdentificacionTutor.values());
        if (id != null) {
            PortalTutorCuentaResponse cuentaPortal = accesoPortalService.obtener(id).orElse(null);
            model.addAttribute("cuentaPortal", cuentaPortal);
            if (!model.containsAttribute("portalForm")) {
                PortalTutorCuentaForm portalForm = new PortalTutorCuentaForm();
                portalForm.setUsername(cuentaPortal == null ? accesoPortalService.sugerirUsername(id) : cuentaPortal.username());
                portalForm.setEmail(cuentaPortal == null ? form.getEmail() : cuentaPortal.email());
                model.addAttribute("portalForm", portalForm);
            }
            List<IdentificacionTutorResponse> historial = identificacionService.historial(id);
            model.addAttribute("identificacionActual", historial.stream()
                    .filter(IdentificacionTutorResponse::actual).findFirst().orElse(null));
            model.addAttribute("historialIdentificaciones", historial.stream()
                    .filter(identificacion -> !identificacion.actual()).toList());
        }
    }

    private void prepararError(Model model, TutorForm form, Long id, RuntimeException excepcion) {
        preparar(model, form, id);
        model.addAttribute("errorOperacion", MensajeErrorFormulario.desde(excepcion));
    }

    private void prepararErrorIdentificacion(Model model, TutorResponse tutor, Long id,
                                              RuntimeException excepcion) {
        preparar(model, TutorForm.desde(tutor), id);
        model.addAttribute("errorIdentificacion", MensajeErrorFormulario.desde(excepcion));
    }

    private TutorResponse tutorAdministrable(Long id) {
        alcance.validarRecurso(ModuloCatalogo.TUTORES, id);
        TutorResponse tutor = service.obtener(id);
        alcance.validarAdministracionInstitucional(tutor.institucionId());
        return tutor;
    }

    private String errorCuentaPortal(Model model, TutorResponse tutor, Long id, RuntimeException excepcion) {
        preparar(model, TutorForm.desde(tutor), id);
        model.addAttribute("errorCuentaPortal", MensajeErrorFormulario.desde(excepcion));
        return "admin/tutor-form";
    }

    private String enlaceActivacion(String token) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/activar-cuenta").queryParam("token", token)
                .build().toUriString();
    }
}
