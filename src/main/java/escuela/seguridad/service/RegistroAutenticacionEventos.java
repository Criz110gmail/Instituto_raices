package escuela.seguridad.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistroAutenticacionEventos {

    private final RegistroAutenticacionService service;

    @EventListener
    public void autenticacionCorrecta(AuthenticationSuccessEvent evento) {
        if (evento.getAuthentication().getPrincipal() instanceof UsuarioPrincipal principal) {
            service.registrarExito(principal);
        }
    }

    @EventListener
    public void credencialesIncorrectas(AuthenticationFailureBadCredentialsEvent evento) {
        service.registrarFallo(evento.getAuthentication().getName());
    }
}
