package escuela.config;

import jakarta.servlet.ServletException;
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
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SesionExpiradaAccessDeniedHandler.class);

    private final AccessDeniedHandler accesoDenegado;

    public SesionExpiradaAccessDeniedHandler() {
        AccessDeniedHandlerImpl handler = new AccessDeniedHandlerImpl();
        handler.setErrorPage("/acceso-denegado");
        this.accesoDenegado = handler;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException excepcion) throws IOException, ServletException {
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        if (ruta.equals("/acceso/comprobar") && (excepcion instanceof InvalidCsrfTokenException
                || excepcion instanceof MissingCsrfTokenException)) {
            preparacionRechazada(request, response);
            return;
        }
        if (ruta.equals("/login") && (excepcion instanceof InvalidCsrfTokenException
                || excepcion instanceof MissingCsrfTokenException)) {
            // Un login abierto en otra pestaña conserva un token viejo incluso si
            // la cookie ya corresponde a una sesión nueva y válida. No borrarla.
            accesoActualizado(request, response);
            return;
        }
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
        // No crear ni borrar cookies desde solicitudes privadas atrasadas: podrían
        // sobrescribir la sesión que otra pestaña acaba de iniciar. El acceso público
        // obtiene su propia sesión sólo cuando necesita un token CSRF.
        response.sendRedirect(request.getContextPath() + acceso);
    }

    void accesoActualizado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        registrarRechazo(request, "login");
        String origen = request.getParameter("origen");
        String acceso = "familias".equals(origen) ? "/familias"
                : "maestros".equals(origen) ? "/maestros/acceso" : "/login";
        response.sendRedirect(request.getContextPath() + acceso + "?accesoActualizado");
    }

    void preparacionRechazada(HttpServletRequest request, HttpServletResponse response) {
        registrarRechazo(request, "preparacion");
        response.setStatus(409);
        response.setHeader("Cache-Control", "no-store");
    }

    private void registrarRechazo(HttpServletRequest request, String fase) {
        // Sólo indicadores; nunca valores, identificadores, tokens, cookies o credenciales.
        LOG.warn("Acceso rechazado fase={} cookieSesion={} sesionValida={} sesionExistente={} campoCsrf={} cabeceraCsrf={}",
                fase, request.isRequestedSessionIdFromCookie(), request.isRequestedSessionIdValid(),
                request.getSession(false) != null, request.getParameter("_csrf") != null,
                request.getHeader("X-CSRF-TOKEN") != null);
    }

    private boolean esTokenDeSesionCaducada(HttpServletRequest request,
                                             AccessDeniedException excepcion) {
        boolean errorCsrf = excepcion instanceof InvalidCsrfTokenException
                || excepcion instanceof MissingCsrfTokenException;
        return errorCsrf && request.getRequestedSessionId() != null
                && !request.isRequestedSessionIdValid();
    }
}
