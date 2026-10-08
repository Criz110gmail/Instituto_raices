package escuela.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SesionController {
    public record Estado(long ahora, long vence, int duracionSegundos, String csrfCabecera, String csrfToken) {}
    @GetMapping("/sesion/estado")
    public ResponseEntity<Estado> estado(HttpServletRequest request) { return responder(request, false); }
    @PostMapping("/sesion/renovar")
    public ResponseEntity<Estado> renovar(HttpServletRequest request) { return responder(request, true); }
    private ResponseEntity<Estado> responder(HttpServletRequest request, boolean renovar) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        var session = request.getSession(false);
        if (session == null || auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
            return ResponseEntity.status(401).header("Cache-Control", "no-store").build();
        synchronized (session) {
            long ahora = System.currentTimeMillis();
            long ultima = SesionActividadFilter.actividad(session);
            if (session.getMaxInactiveInterval() > 0 && ahora - ultima >= session.getMaxInactiveInterval() * 1000L) {
                session.invalidate();
                SecurityContextHolder.clearContext();
                return ResponseEntity.status(401).header("Cache-Control", "no-store").build();
            }
            if (renovar) { ultima = ahora; session.setAttribute(SesionActividadFilter.ACTIVIDAD, ultima); }
            var csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            return ResponseEntity.ok().header("Cache-Control", "no-store").body(new Estado(ahora,
                    ultima + session.getMaxInactiveInterval() * 1000L, session.getMaxInactiveInterval(),
                    csrf == null ? null : csrf.getHeaderName(), csrf == null ? null : csrf.getToken()));
        }
    }
}
