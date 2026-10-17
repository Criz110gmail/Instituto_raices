package escuela.admin.service;

import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerfilSesionService {
    private final UsuarioRepository usuarios;

    // DTO explícito: nunca serializar Usuario ni el principal (contienen credenciales).
    public record Resumen(String usuario, String correo, String institucion, String acceso, String alcance) {}

    public Resumen actual(UsuarioPrincipal principal) {
        if (principal == null) throw new AccessDeniedException("Sesión no disponible");
        if (principal.accesoRecuperacion()) {
            return new Resumen(principal.username(), "", "No vinculada a una institución",
                    "Acceso de recuperación", "Configuración y recuperación del sistema");
        }
        if (principal.usuarioId() == null) throw new AccessDeniedException("Sesión no disponible");
        var usuario = usuarios.findById(principal.usuarioId())
                .orElseThrow(() -> new AccessDeniedException("Sesión no disponible"));
        if (!usuario.getInstitucion().getId().equals(principal.institucionId())
                || usuario.getEstado() != EstadoUsuario.ACTIVO) {
            throw new AccessDeniedException("Sesión no disponible");
        }
        String acceso = switch (usuario.getTipoCuenta()) {
            case ADMINISTRATIVO -> "Administración";
            case PORTAL_TUTOR -> "Portal de familias";
            case PORTAL_MAESTRO -> "Portal de maestros";
        };
        int cantidad = principal.plantelIds().size();
        String alcance = principal.alcanceInstitucional() ? "Toda la institución"
                : cantidad == 0 ? "Sin planteles asignados"
                : cantidad == 1 ? "1 plantel autorizado" : cantidad + " planteles autorizados";
        return new Resumen(usuario.getUsername(), usuario.getEmail(), usuario.getInstitucion().getNombre(), acceso, alcance);
    }
}
