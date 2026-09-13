package escuela.seguridad.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.institucion.entity.Institucion;
import escuela.institucion.repository.InstitucionRepository;
import escuela.seguridad.dto.request.UsuarioRequest;
import escuela.seguridad.dto.response.UsuarioResponse;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.mapper.UsuarioMapper;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final InstitucionRepository institucionRepository;
    private final UsuarioMapper mapper;

    @Override
    public UsuarioResponse crearInvitado(UsuarioRequest request) {
        Institucion institucion = institucion(request.institucionId());
        validar(request, institucion, 0L);
        Usuario usuario = mapper.nuevo(request, institucion);
        usuario.setEstado(EstadoUsuario.INVITADO);
        usuario.setIntentosFallidos(0);
        return mapper.respuesta(repository.saveAndFlush(usuario));
    }

    @Override
    public UsuarioResponse actualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscar(id);
        verificar(usuario, request.version(), "Usuario");
        if (!usuario.getInstitucion().getId().equals(request.institucionId())) {
            throw new ReglaNegocioException("No se puede cambiar la institución de un usuario existente");
        }
        validar(request, usuario.getInstitucion(), id);
        mapper.actualizar(usuario, request, usuario.getInstitucion());
        return mapper.respuesta(repository.saveAndFlush(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return mapper.respuesta(buscar(id));
    }

    @Override
    public UsuarioResponse cambiarEstado(Long id, Long version, EstadoUsuario estado) {
        Usuario usuario = buscar(id);
        verificar(usuario, version, "Usuario");
        if (estado == null) {
            throw new ReglaNegocioException("El estado del usuario es obligatorio");
        }
        if (estado == EstadoUsuario.ACTIVO && usuario.getPasswordHash() == null) {
            throw new ReglaNegocioException("El usuario debe aceptar su invitación antes de activarse");
        }
        usuario.setEstado(estado);
        if (estado != EstadoUsuario.BLOQUEADO) {
            usuario.setBloqueoHasta(null);
            usuario.setIntentosFallidos(0);
        }
        return mapper.respuesta(repository.saveAndFlush(usuario));
    }

    private void validar(UsuarioRequest request, Institucion institucion, Long idExcluido) {
        if (!institucion.isActivo()) {
            throw new ReglaNegocioException("La institución debe estar activa para administrar usuarios");
        }
        String username = limpiar(request.username());
        String correo = email(request.email());
        if (repository.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(
                institucion.getId(), username, idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un usuario con ese nombre en la institución");
        }
        if (repository.existsByInstitucionIdAndEmailIgnoreCaseAndIdNot(
                institucion.getId(), correo, idExcluido)) {
            throw new RecursoDuplicadoException("Ya existe un usuario con ese correo en la institución");
        }
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", id));
    }

    private Institucion institucion(Long id) {
        return institucionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la institución", id));
    }
}
