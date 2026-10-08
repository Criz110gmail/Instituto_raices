package escuela.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.time.Clock;

/** Consultar el reloj no cuenta como actividad: no mantiene una sesión abandonada. */
public class SesionActividadFilter extends OncePerRequestFilter {
    static final String ACTIVIDAD = SesionActividadFilter.class.getName() + ".actividad";
    private final SesionExpiradaAccessDeniedHandler expiracion;
    private final Clock clock;
    public SesionActividadFilter(SesionExpiradaAccessDeniedHandler expiracion) {
        this(expiracion, Clock.systemUTC());
    }
    SesionActividadFilter(SesionExpiradaAccessDeniedHandler expiracion, Clock clock) {
        this.expiracion = expiracion;
        this.clock = clock;
    }
    static long actividad(HttpSession session) {
        Object valor = session.getAttribute(ACTIVIDAD);
        return valor instanceof Long tiempo ? tiempo : session.getLastAccessedTime();
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        var auth = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession(false);
        boolean reloj = ruta.equals("/sesion/estado") || ruta.equals("/sesion/renovar");
        if (session != null && auth != null && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken)) {
            long ahora = clock.millis();
            boolean caducada;
            synchronized (session) {
                long ultima = actividad(session);
                caducada = session.getMaxInactiveInterval() > 0
                        && ahora - ultima >= session.getMaxInactiveInterval() * 1000L;
                if (!caducada) {
                    if (!reloj && !ruta.startsWith("/css/") && !ruta.startsWith("/js/")
                            && !ruta.equals("/favicon.svg")) ultima = ahora;
                    session.setAttribute(ACTIVIDAD, ultima);
                    response.setHeader("X-Session-Expires", Long.toString(ultima + session.getMaxInactiveInterval() * 1000L));
                    response.setHeader("X-Session-Now", Long.toString(ahora));
                } else session.invalidate();
            }
            if (caducada) {
                SecurityContextHolder.clearContext();
                if (reloj) {
                    response.setStatus(401);
                    response.setHeader("Cache-Control", "no-store");
                } else expiracion.redirigir(request, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
