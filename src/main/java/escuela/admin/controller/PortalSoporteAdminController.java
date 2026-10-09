package escuela.admin.controller;

import escuela.auditoria.entity.AccionAuditoria;
import escuela.auditoria.service.RegistroAuditoriaService;
import escuela.portal.service.PortalTutorService;
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
import java.util.Set;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import escuela.calificacion.service.CalificacionService;
import escuela.portal.service.PortalBoletaService;
import escuela.admin.service.JasperBoletaService;
import escuela.admin.service.JasperComprobantePagoService;
import escuela.portal.service.PortalPagoService;
import org.springframework.http.HttpHeaders;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import escuela.horario.service.HorarioClaseService;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/portal-soporte")
public class PortalSoporteAdminController {
    private final TutorRepository tutores;
    private final AlcanceDatosService alcance;
    private final PortalTutorService portal;
    private final RegistroAuditoriaService auditoria;
    private final CalificacionService calificaciones;
    private final PortalBoletaService boletas;
    private final JasperBoletaService jasperBoletas;
    private final PortalPagoService pagosPortal;
    private final JasperComprobantePagoService jasperComprobante;
    private final HorarioClaseService horarios;

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
        return vista(tutorId, alumnoId, paginaEventos, paginaCargos, paginaAvisos, paginaPagos,
                null, null, null, admin, model);
    }

    @GetMapping("/{tutorId}/{seccion}")
    @Transactional
    String verSeccion(@PathVariable Long tutorId, @PathVariable String seccion,
                      @RequestParam(required=false) Long alumnoId,
                      @RequestParam(defaultValue="0") int pagina,
                      @RequestParam(defaultValue="0") int paginaCargos,
                      @RequestParam(defaultValue="0") int paginaPagos,
                      @RequestParam(defaultValue="0") int paginaSaldo,
                      @RequestParam(required=false) Integer mes,
                      @RequestParam(required=false) Integer anio,
                      @AuthenticationPrincipal UsuarioPrincipal admin, Model model) {
        String destino = seccion == null ? "" : seccion.toUpperCase(java.util.Locale.ROOT);
        if (!Set.of("AVISOS", "AGENDA", "PAGOS", "CALIFICACIONES", "BOLETAS", "HORARIO").contains(destino)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND);
        }
        model.addAttribute("paginaSaldo", Math.max(0, paginaSaldo));
        return vista(tutorId, alumnoId, Set.of("AGENDA", "CALIFICACIONES", "BOLETAS").contains(destino) ? pagina : 0,
                destino.equals("PAGOS") ? paginaCargos : 0,
                destino.equals("AVISOS") ? pagina : 0,
                destino.equals("PAGOS") ? paginaPagos : 0,
                destino.equals("PAGOS") ? mes : null,
                destino.equals("PAGOS") ? anio : null,
                destino, admin, model);
    }

    private String vista(Long tutorId, Long alumnoId, int paginaEventos, int paginaCargos,
                         int paginaAvisos, int paginaPagos, Integer mesPago, Integer anioPago,
                         String seccion,
                         UsuarioPrincipal admin, Model model) {
        validarAdministradorSoporte(admin);
        alcance.validarAdministracionInstitucional(admin.institucionId());
        Tutor tutor = tutores.findById(tutorId).orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("El tutor no está disponible"));
        if (!tutor.isActivo() || tutor.getUsuario() == null
                || tutor.getUsuario().getEstado() != escuela.seguridad.entity.EstadoUsuario.ACTIVO
                || !tutor.getInstitucion().getId().equals(admin.institucionId())) {
            throw new org.springframework.security.access.AccessDeniedException("El tutor no tiene una cuenta familiar activa");
        }
        UsuarioPrincipal vista = principalTutor(tutor, admin.institucionId());
        var resultadoPortal = "PAGOS".equals(seccion)
                ? portal.consultarPagosComoSoporte(vista, alumnoId, paginaCargos, paginaPagos,
                        mesPago, anioPago)
                : portal.consultarComoSoporte(vista, alumnoId, paginaEventos, paginaCargos,
                        paginaAvisos, paginaPagos);
        model.addAttribute("portal", resultadoPortal);
        model.addAttribute("soporte", true);
        model.addAttribute("tutorSoporteId", tutorId);
        if (seccion != null) {
            model.addAttribute("seccion", seccion);
            model.addAttribute("rutaInicio", "/admin/portal-soporte/" + tutorId);
            model.addAttribute("rutaSeccion", "/admin/portal-soporte/" + tutorId + "/"
                    + seccion.toLowerCase(java.util.Locale.ROOT));
            if (seccion.equals("PAGOS")) {
                int paginaSaldo = model.getAttribute("paginaSaldo") instanceof Integer numero ? numero : 0;
                model.addAttribute("saldoFavor", portal.saldoFavor(vista, resultadoPortal.hijo() == null ? null : resultadoPortal.hijo().alumnoId(), paginaSaldo));
                model.addAttribute("mesPago", mesPago);
                model.addAttribute("anioPago", anioPago);
                model.addAttribute("nombreMesPago", mesPago == null ? "Todos los meses"
                        : java.time.Month.of(mesPago).getDisplayName(java.time.format.TextStyle.FULL,
                        java.util.Locale.forLanguageTag("es-MX")));
                model.addAttribute("aniosPago", resultadoPortal.hijo() == null
                        ? java.util.List.of()
                        : portal.aniosPagos(vista, resultadoPortal.hijo().alumnoId()));
            }
            if (seccion.equals("CALIFICACIONES")) {
                model.addAttribute("calificaciones", resultadoPortal.hijo() == null
                        ? org.springframework.data.domain.Page.empty()
                        : calificaciones.publicadasAlumno(resultadoPortal.hijo().alumnoId(),
                        Math.max(0, paginaEventos), 10));
            }
            if (seccion.equals("BOLETAS")) {
                model.addAttribute("boletas", resultadoPortal.hijo() == null
                        ? org.springframework.data.domain.Page.empty()
                        : boletas.listar(vista, resultadoPortal.hijo().alumnoId(), Math.max(0, paginaEventos)));
            }
            if (seccion.equals("HORARIO")) {
                java.time.LocalDate fecha=java.time.LocalDate.now();model.addAttribute("fechaHorario",fecha);
                model.addAttribute("horario",resultadoPortal.hijo()==null?java.util.List.of():horarios.horarioAlumno(resultadoPortal.hijo().alumnoId(),fecha));
            }
        }
        auditoria.registrar(admin.institucionId(), AccionAuditoria.PORTAL_TUTOR_SOPORTE, "TUTOR", tutorId,
                "Consulta de soporte del portal familiar", java.util.Map.of("tutorUsuario", tutor.getUsuario().getUsername()));
        return seccion == null ? "portal/inicio" : "portal/seccion";
    }

    @GetMapping("/{tutorId}/boletas/{inscripcionId}/pdf")
    @Transactional(readOnly = true)
    void boletaPdf(@PathVariable Long tutorId, @PathVariable Long inscripcionId,
                   @RequestParam Long alumnoId, @AuthenticationPrincipal UsuarioPrincipal admin,
                   jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        validarAdministradorSoporte(admin);
        alcance.validarAdministracionInstitucional(admin.institucionId());
        Tutor tutor = tutorActivo(tutorId, admin.institucionId());
        var detalle = boletas.detalle(principalTutor(tutor, admin.institucionId()), alumnoId, inscripcionId);
        auditoria.registrar(admin.institucionId(), AccionAuditoria.PORTAL_TUTOR_SOPORTE, "TUTOR", tutorId,
                "Consulta de boleta desde soporte", java.util.Map.of("inscripcionId", inscripcionId));
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" +
                URLEncoder.encode("boleta-" + detalle.ciclo() + ".pdf", StandardCharsets.UTF_8));
        jasperBoletas.exportar(detalle, response.getOutputStream());
    }

    @GetMapping("/{tutorId}/pagos/{pagoId}/comprobante-pago")
    @Transactional(readOnly = true)
    void comprobantePago(@PathVariable Long tutorId, @PathVariable Long pagoId,
                         @AuthenticationPrincipal UsuarioPrincipal admin,
                         jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        validarAdministradorSoporte(admin);
        alcance.validarAdministracionInstitucional(admin.institucionId());
        Tutor tutor = tutorActivo(tutorId, admin.institucionId());
        var pago = pagosPortal.comprobante(principalTutor(tutor, admin.institucionId()), pagoId);
        auditoria.registrar(admin.institucionId(), AccionAuditoria.PORTAL_TUTOR_SOPORTE, "PAGO", pagoId,
                "Consulta de comprobante de pago desde soporte", java.util.Map.of("tutorId", tutorId));
        response.setContentType("application/pdf");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" +
                URLEncoder.encode("comprobante-" + pago.folio() + ".pdf", StandardCharsets.UTF_8));
        jasperComprobante.exportar(pago, response.getOutputStream());
    }

    private Tutor tutorActivo(Long tutorId, Long institucionId) {
        Tutor tutor = tutores.findById(tutorId).orElseThrow(() ->
                new org.springframework.security.access.AccessDeniedException("El tutor no está disponible"));
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
        if (!permitido) {
            throw new org.springframework.security.access.AccessDeniedException("No tienes acceso al soporte del portal familiar");
        }
    }
}
