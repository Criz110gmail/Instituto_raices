package escuela.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import org.springframework.web.filter.OncePerRequestFilter;

/** Rechaza envíos de sesiones inexistentes antes de que CSRF cree otra cookie. */
public class SesionInvalidaPostFilter extends OncePerRequestFilter {
    private final SesionExpiradaAccessDeniedHandler expiracion;
    public SesionInvalidaPostFilter(SesionExpiradaAccessDeniedHandler expiracion) {
        this.expiracion = expiracion;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        if (!Set.of("GET", "HEAD", "OPTIONS", "TRACE").contains(request.getMethod())
                && request.getRequestedSessionId() != null && !request.isRequestedSessionIdValid()
                && request.getSession(false) == null) {
            String ruta = request.getRequestURI().substring(request.getContextPath().length());
            if (ruta.equals("/login")) expiracion.accesoActualizado(request, response);
            else if (ruta.equals("/sesion/renovar")) {
                response.setStatus(401);
                response.setHeader("Cache-Control", "no-store");
            } else expiracion.redirigir(request, response);
            return;
        }
        // Una sesión existente continúa por el filtro CSRF normal, nunca lo omite.
        chain.doFilter(request, response);
    }
}
