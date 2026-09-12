package escuela.config.audit;

import escuela.seguridad.service.UsuarioPrincipal;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.context.SecurityContextHolder;

public class ActorAuditoriaEntityListener {

    @PrePersist
    public void antesDeCrear(EntidadAuditable entidad) {
        if (!hayUsuarioPersistido()) {
            entidad.setCreadoPorId(null);
            entidad.setActualizadoPorId(null);
        }
    }

    @PreUpdate
    public void antesDeActualizar(EntidadAuditable entidad) {
        if (!hayUsuarioPersistido()) entidad.setActualizadoPorId(null);
    }

    private boolean hayUsuarioPersistido() {
        var autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return autenticacion != null && autenticacion.isAuthenticated()
                && autenticacion.getPrincipal() instanceof UsuarioPrincipal principal
                && !principal.accesoRecuperacion() && principal.usuarioId() != null;
    }
}
