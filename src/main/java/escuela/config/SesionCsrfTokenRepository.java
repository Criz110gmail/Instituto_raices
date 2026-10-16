package escuela.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/** Una respuesta privada atrasada nunca puede resucitar una sesión cerrada. */
public final class SesionCsrfTokenRepository implements CsrfTokenRepository {
    private static final String ATRIBUTO = SesionCsrfTokenRepository.class.getName() + ".TOKEN";
    private final HttpSessionCsrfTokenRepository generador = new HttpSessionCsrfTokenRepository();

    @Override public CsrfToken generateToken(HttpServletRequest request) {
        return generador.generateToken(request);
    }
    @Override public CsrfToken loadToken(HttpServletRequest request) {
        var sesion = request.getSession(false);
        if (sesion == null) return null;
        try { return (CsrfToken) sesion.getAttribute(ATRIBUTO); }
        catch (IllegalStateException cerradaDuranteSolicitud) { return null; }
    }
    @Override public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        var sesion = request.getSession(false);
        if (sesion == null && token != null && permiteSesionAnonima(request)) sesion = request.getSession(true);
        if (sesion == null) return;
        // Usar la referencia capturada: si logout la invalida no pedir otra sesión.
        try {
            if (token == null) sesion.removeAttribute(ATRIBUTO);
            else sesion.setAttribute(ATRIBUTO, token);
        } catch (IllegalStateException cerradaDuranteSolicitud) {
            // Nunca emitir otra cookie desde una respuesta de la sesión anterior.
        }
    }
    private boolean permiteSesionAnonima(HttpServletRequest request) {
        if (!"GET".equals(request.getMethod())) return false;
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        return ruta.equals("/acceso/csrf") || ruta.equals("/activar-cuenta")
                || ruta.equals("/restablecer-password");
    }
}
