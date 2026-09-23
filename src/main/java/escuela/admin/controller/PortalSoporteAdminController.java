package escuela.admin.controller;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.portal.service.PortalTutorService;
import escuela.portal.service.PortalPagoService;
import escuela.portal.dto.PortalPagoForm;
import escuela.admin.dto.ResultadoAutocompletado;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.service.InstitucionService;
import escuela.seguridad.service.AlcanceDatosService;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.time.ZoneId;
import java.time.LocalDateTime;

import java.util.Set;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/portal-soporte")
public class PortalSoporteAdminController {
    private final TutorRepository tutores;
    private final AlcanceDatosService alcance;
    private final PortalTutorService portal;
    private final PortalPagoService pagos;
    private final InstitucionService instituciones;
    private final RegistroAuditoriaService auditoria;

    @GetMapping
    @Transactional(readOnly = true)
    String listado(@RequestParam(defaultValue="") String q,
                   @AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        validarAdministradorSoporte(principal);
        alcance.validarAdministracionInstitucional(principal.institucionId());
        String texto = q == null ? "" : q.trim();
        var resultados = tutores.buscarParaAutocompletado(principal.institucionId(), texto,
                PageRequest.of(0, 20)).getContent().stream()
                .filter(t -> t.getUsuario() != null
                        && t.getUsuario().getEstado() == escuela.seguridad.entity.EstadoUsuario.ACTIVO)
                .toList();
        model.addAttribute("tutores", resultados);
        model.addAttribute("q", texto);
        return "admin/portal-soporte";
    }

    @GetMapping("/{tutorId}")
    @Transactional
    String ver(@PathVariable Long tutorId, @RequestParam(required=false) Long alumnoId,
               @RequestParam(defaultValue="0") int paginaEventos,
               @RequestParam(defaultValue="0") int paginaCargos,
               @RequestParam(defaultValue="0") int paginaAvisos,
               @RequestParam(defaultValue="0") int paginaPagos,
                   @AuthenticationPrincipal UsuarioPrincipal admin, Model model) {
        validarAdministradorSoporte(admin);
        alcance.validarAdministracionInstitucional(admin.institucionId());
        Tutor tutor = tutores.findById(tutorId).orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("El tutor no está disponible"));
        if (!tutor.isActivo() || tutor.getUsuario() == null
                || tutor.getUsuario().getEstado() != escuela.seguridad.entity.EstadoUsuario.ACTIVO
                || !tutor.getInstitucion().getId().equals(admin.institucionId())) {
            throw new org.springframework.security.access.AccessDeniedException("El tutor no tiene una cuenta familiar activa");
        }
        UsuarioPrincipal vista = principalTutor(tutor, admin.institucionId());
        model.addAttribute("portal", portal.consultarComoSoporte(vista, alumnoId, paginaEventos, paginaCargos, paginaAvisos, paginaPagos));
        model.addAttribute("soporte", true);
        model.addAttribute("tutorSoporteId", tutorId);
        auditoria.registrar(admin.institucionId(), AccionAuditoria.PORTAL_TUTOR_SOPORTE, "TUTOR", tutorId,
                "Consulta de soporte del portal familiar", java.util.Map.of("tutorUsuario", tutor.getUsuario().getUsername()));
        return "portal/inicio";
    }

    @GetMapping("/{tutorId}/pagos/reportar")
    @Transactional(readOnly = true)
    String formularioPago(@PathVariable Long tutorId, @AuthenticationPrincipal UsuarioPrincipal admin, Model model) {
        validarAdministradorSoporte(admin);
        Tutor tutor = tutorActivo(tutorId, admin.institucionId());
        UsuarioPrincipal vista = principalTutor(tutor, admin.institucionId());
        PortalPagoForm form = new PortalPagoForm();
        form.setFechaPago(LocalDateTime.now(ZoneId.of(instituciones.obtener(admin.institucionId()).zonaHoraria()))
                .withSecond(0).withNano(0));
        form.getSolicitudes().add(new escuela.portal.dto.PortalSolicitudPagoForm());
        prepararFormularioPago(vista, tutorId, form, model);
        return "portal/pago-form";
    }

    @PostMapping(value = "/{tutorId}/pagos/reportar", consumes = "multipart/form-data")
    @Transactional
    String guardarPago(@PathVariable Long tutorId, @Valid @ModelAttribute("form") PortalPagoForm form,
                       BindingResult errores, @RequestParam(name = "comprobantes", required = false) List<MultipartFile> files,
                       @AuthenticationPrincipal UsuarioPrincipal admin, Model model, RedirectAttributes flash) {
        validarAdministradorSoporte(admin);
        Tutor tutor = tutorActivo(tutorId, admin.institucionId());
        UsuarioPrincipal vista = principalTutor(tutor, admin.institucionId());
        if (errores.hasErrors()) {
            prepararFormularioPago(vista, tutorId, form, model);
            return "portal/pago-form";
        }
        try {
            pagos.reportar(vista, form, files);
            auditoria.registrar(admin.institucionId(), AccionAuditoria.PORTAL_TUTOR_SOPORTE, "TUTOR", tutorId,
                    "Transferencia reportada por administración en modo soporte", java.util.Map.of(
                            "tutorUsuario", tutor.getUsuario().getUsername(), "operacion", "REPORTAR_TRANSFERENCIA"));
            flash.addFlashAttribute("mensajePortal", "Transferencia enviada a revisión desde el modo soporte.");
            return "redirect:/admin/portal-soporte/" + tutorId;
        } catch (ReglaNegocioException ex) {
            model.addAttribute("error", ex.getMessage());
            prepararFormularioPago(vista, tutorId, form, model);
            return "portal/pago-form";
        }
    }

    @GetMapping("/{tutorId}/pagos/cargos")
    @ResponseBody
    ResultadoAutocompletado cargosPago(@PathVariable Long tutorId, @RequestParam(defaultValue = "") String q,
                                       @AuthenticationPrincipal UsuarioPrincipal admin) {
        validarAdministradorSoporte(admin);
        Tutor tutor = tutorActivo(tutorId, admin.institucionId());
        return pagos.buscarCargos(principalTutor(tutor, admin.institucionId()), q);
    }

    @GetMapping("/{tutorId}/pagos/cuentas")
    @ResponseBody
    ResultadoAutocompletado cuentasPago(@PathVariable Long tutorId, @RequestParam Long plantelId,
                                        @RequestParam(defaultValue = "") String q,
                                        @AuthenticationPrincipal UsuarioPrincipal admin) {
        validarAdministradorSoporte(admin);
        Tutor tutor = tutorActivo(tutorId, admin.institucionId());
        return pagos.buscarCuentas(principalTutor(tutor, admin.institucionId()), plantelId, q);
    }

    private void prepararFormularioPago(UsuarioPrincipal vista, Long tutorId, PortalPagoForm form, Model model) {
        model.addAttribute("form", form);
        model.addAttribute("planteles", pagos.planteles(vista));
        model.addAttribute("moneda", instituciones.obtener(vista.institucionId()).monedaPredeterminada());
        model.addAttribute("soporte", true);
        model.addAttribute("tutorSoporteId", tutorId);
        model.addAttribute("pagoSoporte", true);
    }

    private Tutor tutorActivo(Long tutorId, Long institucionId) {
        Tutor tutor = tutores.findById(tutorId).orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("El tutor no está disponible"));
        if (!tutor.isActivo() || tutor.getUsuario() == null
                || tutor.getUsuario().getEstado() != escuela.seguridad.entity.EstadoUsuario.ACTIVO
                || !tutor.getInstitucion().getId().equals(institucionId)) {
            throw new org.springframework.security.access.AccessDeniedException("El tutor no tiene una cuenta familiar activa");
        }
        return tutor;
    }

    private UsuarioPrincipal principalTutor(Tutor tutor, Long institucionId) {
        return new UsuarioPrincipal(tutor.getUsuario().getId(), institucionId, Set.of(), true, false,
                tutor.getUsuario().getUsername(), "", Set.of(new SimpleGrantedAuthority("PORTAL_TUTOR_ACCEDER")));
    }

    private void validarAdministradorSoporte(UsuarioPrincipal principal) {
        boolean permitido = principal != null && principal.authorities().stream()
                .anyMatch(a -> a.getAuthority().equals("PORTAL_TUTOR_SOPORTE"));
        boolean administrador = principal != null && principal.authorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROL_ADMINISTRAR"));
        if (!permitido || !administrador) {
            throw new org.springframework.security.access.AccessDeniedException("El soporte del portal requiere permisos administrativos");
        }
    }
}
