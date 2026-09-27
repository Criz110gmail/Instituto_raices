package escuela.tutor.dto.response;

import escuela.seguridad.entity.EstadoUsuario;

public record PortalTutorCuentaResponse(
        Long usuarioId,
        String username,
        String email,
        EstadoUsuario estado,
        boolean credencialConfigurada,
        Long version
) {
}
