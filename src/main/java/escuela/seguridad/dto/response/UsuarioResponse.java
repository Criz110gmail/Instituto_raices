package escuela.seguridad.dto.response;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.seguridad.entity.EstadoUsuario;

public record UsuarioResponse(
        Long id,
        Long institucionId,
        String username,
        String email,
        EstadoUsuario estado,
        boolean credencialConfigurada,
        boolean fotografiaConfigurada,
        AuditoriaResponse auditoria
) {
}
