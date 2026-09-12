package escuela.seguridad.service;

import escuela.seguridad.dto.request.UsuarioRequest;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.EstadoUsuario;

import java.util.List;

public interface UsuarioService {
    UsuarioResponse crearInvitado(UsuarioRequest request);
    UsuarioResponse actualizar(Long id, UsuarioRequest request);
    UsuarioResponse obtener(Long id);
    UsuarioResponse cambiarEstado(Long id, Long version, EstadoUsuario estado);
    List<UsuarioResponse> listarPorInstitucion(Long institucionId);
}
