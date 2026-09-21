package escuela.auditoria.support;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class CorrelacionAuditoriaFilter extends OncePerRequestFilter {
    public static final String CABECERA = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String correlacion = CorrelacionAuditoria.iniciar();
        response.setHeader(CABECERA, correlacion);
        try {
            filterChain.doFilter(request, response);
        } finally {
            CorrelacionAuditoria.limpiar();
        }
    }
}
