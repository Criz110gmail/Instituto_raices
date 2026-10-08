package escuela.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SesionExpiradaAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler accesoDenegado;

    public SesionExpiradaAccessDeniedHandler() {
        AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
        handler.setErrorPage("/acceso-denegado");
        this.accesoDenegado = handler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException excepcion) throws IOException, ServletException {
        if (esTokenDeSesionCaducada(request, excepcion)) {
            redirigir(request, response);
            return;
        }
        accesoDenegado.handle(request, response, excepcion);
    }

    void redirigir(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean envioAcceso = ruta.equals("/logout") || ruta.equals("/login");
        String origen = envioAcceso ? request.getParameter("origen") : null;
        String acceso = ruta.equals("/maestros") || ruta.startsWith("/maestros/") || "maestros".equals(origen)
                ? "/maestros/acceso?sesionExpirada"
                : ruta.equals("/portal") || ruta.startsWith("/portal/") || ruta.equals("/familias") || "familias".equals(origen)
                ? "/familias?sesionExpirada" : "/login?sesionExpirada";
        Cookie sesionCaducada = new Cookie("JSESSIONID", "");
        sesionCaducada.setPath(request.getContextPath().isBlank() ? "/" : request.getContextPath());
        sesionCaducada.setHttpOnly(true);
        sesionCaducada.setSecure(request.isSecure());
        sesionCaducada.setMaxAge(0);
        response.addCookie(sesionCaducada);
        response.sendRedirect(request.getContextPath() + acceso);
    }

    private boolean esTokenDeSesionCaducada(HttpServletRequest request,
                                             AccessDeniedException excepcion) {
        boolean errorCsrf = excepcion instanceof InvalidCsrfTokenException
                || excepcion instanceof MissingCsrfTokenException;
        return errorCsrf && request.getRequestedSessionId() != null
                && !request.isRequestedSessionIdValid();
    }
}
