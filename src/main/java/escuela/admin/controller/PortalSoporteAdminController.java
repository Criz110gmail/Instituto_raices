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

@Controller @RequiredArgsConstructor @RequestMapping("/admin/portal-soporte")
public class PortalSoporteAdminController {
    private final TutorRepository tutores;
    private final AlcanceDatosService alcance;
    private final PortalTutorService portal;
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
