package escuela.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/** Una cookie vieja no debe bloquear el acceso público ni sus archivos estáticos. */
public class SesionInvalidaFilter extends OncePerRequestFilter {
    private static final Set<String> ACCESO_PUBLICO = Set.of("/", "/login", "/familias",
            "/maestros/acceso", "/activar-cuenta", "/restablecer-password", "/acceso/csrf",
            "/acceso-denegado", "/salud", "/actuator/health", "/favicon.svg", "/error");
    private final SesionExpiradaAccessDeniedHandler expiracion;

    public SesionInvalidaFilter(SesionExpiradaAccessDeniedHandler expiracion) {
        this.expiracion = expiracion;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean consulta = "GET".equals(request.getMethod()) || "HEAD".equals(request.getMethod());
        boolean publico = consulta && (ACCESO_PUBLICO.contains(ruta)
                || ruta.startsWith("/css/") || ruta.startsWith("/js/"));
        var auth = SecurityContextHolder.getContext().getAuthentication();
        boolean anonimo = auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken;
        if (!publico && anonimo && request.getRequestedSessionId() != null
                && !request.isRequestedSessionIdValid() && request.getSession(false) == null) {
            if (ruta.equals("/sesion/estado") || ruta.equals("/sesion/renovar")) {
                response.setStatus(401);
                response.setHeader("Cache-Control", "no-store");
            } else expiracion.redirigir(request, response);
            return;
        }
        // No concede acceso: autorización y CSRF siguen a cargo de Spring Security.
        chain.doFilter(request, response);
    }
}
