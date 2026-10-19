package escuela.seguridad.service;

import org.springframework.security.core.Authentication;

/** Destinos internos fijos: navegar no concede autoridades nuevas. */
public final class DestinoPortalSesion {
    private DestinoPortalSesion() {}
    public static String inicio(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) return "/";
        boolean maestro=authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("PORTAL_MAESTRO_ACCEDER"));
        boolean familia=authentication.getAuthorities().stream().anyMatch(a->a.getAuthority().equals("PORTAL_TUTOR_ACCEDER"));
        if (maestro && !familia) return "/maestros";
        if (familia && !maestro) return "/portal";
        return "/admin";
    }
}
