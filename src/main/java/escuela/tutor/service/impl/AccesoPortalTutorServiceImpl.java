package escuela.tutor.service.impl;

import escuela.common.exception.RecursoDuplicadoException;
import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.TipoCuentaUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.tutor.dto.request.PortalTutorCuentaRequest;
import escuela.tutor.dto.response.PortalTutorCuentaResponse;
import escuela.tutor.entity.Tutor;
import escuela.tutor.repository.TutorRepository;
import escuela.tutor.service.AccesoPortalTutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;

import static escuela.common.mapper.NormalizacionTexto.email;
import static escuela.common.mapper.NormalizacionTexto.limpiar;
import static escuela.common.service.ValidacionVersion.verificar;

@Service
@RequiredArgsConstructor
@Transactional
public class AccesoPortalTutorServiceImpl implements AccesoPortalTutorService {

    private final TutorRepository tutorRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<PortalTutorCuentaResponse> obtener(Long tutorId) {
        Tutor tutor = tutor(tutorId);
        return Optional.ofNullable(tutor.getUsuario()).map(this::respuesta);
    }

    @Override
    @Transactional(readOnly = true)
    public String sugerirUsername(Long tutorId) {
        Tutor tutor = tutor(tutorId);
        String base = segmento(tutor.getNombres()) + "." + segmento(tutor.getPrimerApellido());
        if (base.equals(".")) base = "familia";
        String candidato = limitar(base, 80);
        int sufijo = 2;
        while (usuarioRepository.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(
                tutor.getInstitucion().getId(), candidato, 0L)) {
            String finalSufijo = String.valueOf(sufijo++);
            candidato = limitar(base, 80 - finalSufijo.length()) + finalSufijo;
        }
        return candidato;
    }

    @Override
    public PortalTutorCuentaResponse crear(Long tutorId, PortalTutorCuentaRequest request) {
        Tutor tutor = tutorRepository.findByIdForUpdate(tutorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", tutorId));
        if (!tutor.isActivo()) {
            throw new ReglaNegocioException("El tutor debe estar activo para habilitar su portal");
        }
        if (tutor.getUsuario() != null) {
            throw new RecursoDuplicadoException("El tutor ya tiene una cuenta para el portal familiar");
        }
        String username = limpiar(request.username());
        String correo = email(request.email());
        Long institucionId = tutor.getInstitucion().getId();
        if (username == null || correo == null) {
            throw new ReglaNegocioException("El usuario y el correo son obligatorios");
        }
        if (usuarioRepository.existsByInstitucionIdAndUsernameIgnoreCaseAndIdNot(institucionId, username, 0L)) {
            throw new RecursoDuplicadoException("Ese nombre de usuario ya existe; puedes escribir uno diferente");
        }
        if (usuarioRepository.existsByInstitucionIdAndEmailIgnoreCaseAndIdNot(institucionId, correo, 0L)) {
            throw new RecursoDuplicadoException("Ese correo ya pertenece a otra cuenta de la institución");
        }
        Usuario usuario = new Usuario();
        usuario.setInstitucion(tutor.getInstitucion());
        usuario.setUsername(username);
        usuario.setEmail(correo);
        usuario.setTipoCuenta(TipoCuentaUsuario.PORTAL_TUTOR);
        usuario.setEstado(EstadoUsuario.INVITADO);
        usuario.setIntentosFallidos(0);
        usuarioRepository.saveAndFlush(usuario);
        tutor.setUsuario(usuario);
        tutorRepository.saveAndFlush(tutor);
        return respuesta(usuario);
    }

    @Override
    public PortalTutorCuentaResponse cambiarDisponibilidad(Long tutorId, Long version, boolean activar) {
        Tutor tutor = tutorRepository.findByIdForUpdate(tutorId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", tutorId));
        Usuario usuario = cuenta(tutor);
        verificar(usuario, version, "Cuenta del portal");
        if (activar && !tutor.isActivo()) {
            throw new ReglaNegocioException("Activa primero al tutor antes de habilitar su portal");
        }
        usuario.setEstado(activar
                ? (usuario.getPasswordHash() == null ? EstadoUsuario.INVITADO : EstadoUsuario.ACTIVO)
                : EstadoUsuario.INACTIVO);
        usuario.setBloqueoHasta(null);
        usuario.setIntentosFallidos(0);
        return respuesta(usuarioRepository.saveAndFlush(usuario));
    }

    private Tutor tutor(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("el tutor", id));
    }

    private Usuario cuenta(Tutor tutor) {
        Usuario usuario = tutor.getUsuario();
        if (usuario == null || usuario.getTipoCuenta() != TipoCuentaUsuario.PORTAL_TUTOR) {
            throw new ReglaNegocioException("El tutor no tiene una cuenta del portal familiar");
        }
        return usuario;
    }

    private PortalTutorCuentaResponse respuesta(Usuario usuario) {
        if (usuario.getTipoCuenta() != TipoCuentaUsuario.PORTAL_TUTOR) {
            throw new ReglaNegocioException("La cuenta vinculada no pertenece al portal familiar");
        }
        return new PortalTutorCuentaResponse(usuario.getId(), usuario.getUsername(), usuario.getEmail(),
                usuario.getEstado(), usuario.getPasswordHash() != null, usuario.getVersion());
    }

    private String segmento(String texto) {
        if (texto == null) return "";
        String primero = texto.trim().split("\\s+")[0];
        return Normalizer.normalize(primero, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_-]", "");
    }

    private String limitar(String texto, int longitud) {
        return texto.length() <= longitud ? texto : texto.substring(0, longitud);
    }
}
