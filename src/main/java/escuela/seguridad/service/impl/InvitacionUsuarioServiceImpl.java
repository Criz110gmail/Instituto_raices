package escuela.seguridad.service.impl;

import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.dto.request.ActivacionUsuarioRequest;
import escuela.seguridad.dto.response.InvitacionEmitidaResponse;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.InvitacionUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.InvitacionUsuarioRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.InvitacionUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@Transactional
public class InvitacionUsuarioServiceImpl implements InvitacionUsuarioService {

    private static final Duration VIGENCIA_MAXIMA = Duration.ofDays(7);
    private static final Duration VIGENCIA_MINIMA = Duration.ofMinutes(1);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final InvitacionUsuarioRepository invitacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public InvitacionEmitidaResponse emitir(Long usuarioId, Duration vigencia) {
        validarVigencia(vigencia);
        Usuario usuario = usuarioRepository.buscarPorIdConBloqueo(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", usuarioId));
        if (usuario.getEstado() != EstadoUsuario.INVITADO) {
            throw new ReglaNegocioException("Sólo se pueden invitar usuarios pendientes de activación");
        }

        Instant ahora = Instant.now();
        var vigentes = invitacionRepository
                .findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(usuarioId);
        vigentes.forEach(invitacion -> invitacion.setRevocadoEn(ahora));
        if (!vigentes.isEmpty()) {
            invitacionRepository.saveAllAndFlush(vigentes);
        }

        String token = generarToken();
        InvitacionUsuario invitacion = new InvitacionUsuario();
        invitacion.setUsuario(usuario);
        invitacion.setTokenHash(hash(token));
        invitacion.setExpiraEn(ahora.plus(vigencia));
        invitacionRepository.saveAndFlush(invitacion);
        return new InvitacionEmitidaResponse(token, invitacion.getExpiraEn());
    }

    @Override
    public void activar(ActivacionUsuarioRequest request) {
        if (request.password() == null || request.password().length() < 12
                || request.password().getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña debe tener entre 12 y 72 caracteres");
        }
        InvitacionUsuario invitacion = invitacionRepository.buscarPorTokenHashConBloqueo(hash(request.token()))
                .orElseThrow(() -> new ReglaNegocioException("La invitación no es válida"));
        Instant ahora = Instant.now();
        if (invitacion.getUsadoEn() != null || invitacion.getRevocadoEn() != null
                || !invitacion.getExpiraEn().isAfter(ahora)) {
            throw new ReglaNegocioException("La invitación venció o ya fue utilizada");
        }
        Usuario usuario = invitacion.getUsuario();
        if (usuario.getEstado() == EstadoUsuario.INACTIVO) {
            throw new ReglaNegocioException("El usuario está inactivo");
        }
        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setBloqueoHasta(null);
        usuario.setIntentosFallidos(0);
        invitacion.setUsadoEn(ahora);
        usuarioRepository.save(usuario);
        invitacionRepository.saveAndFlush(invitacion);
    }

    private void validarVigencia(Duration vigencia) {
        if (vigencia == null || vigencia.compareTo(VIGENCIA_MINIMA) < 0
                || vigencia.compareTo(VIGENCIA_MAXIMA) > 0) {
            throw new ReglaNegocioException("La invitación debe vencer entre un minuto y siete días");
        }
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        if (token == null || token.isBlank()) {
            throw new ReglaNegocioException("La invitación no es válida");
        }
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}
