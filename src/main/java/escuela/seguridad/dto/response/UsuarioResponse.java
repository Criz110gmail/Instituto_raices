package escuela.seguridad.dto.response;

import escuela.common.dto.response.AuditoriaResponse;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.TipoCuentaUsuario;

public record UsuarioResponse(
        Long id,
        Long institucionId,
        String username,
        String email,
        EstadoUsuario estado,
        TipoCuentaUsuario tipoCuenta,
        boolean credencialConfigurada,
        boolean fotografiaConfigurada,
        AuditoriaResponse auditoria
) {
}
