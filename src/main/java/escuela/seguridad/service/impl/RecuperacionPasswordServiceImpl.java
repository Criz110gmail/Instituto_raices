package escuela.seguridad.service.impl;

import escuela.common.exception.RecursoNoEncontradoException;
import escuela.common.exception.ReglaNegocioException;
import escuela.seguridad.dto.request.RestablecimientoPasswordRequest;
import escuela.seguridad.dto.response.RecuperacionPasswordEmitidaResponse;
import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.RecuperacionPassword;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.RecuperacionPasswordRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.RecuperacionPasswordService;
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
public class RecuperacionPasswordServiceImpl implements RecuperacionPasswordService {

    private static final Duration VIGENCIA_MAXIMA = Duration.ofHours(2);
    private static final Duration VIGENCIA_MINIMA = Duration.ofMinutes(5);
    private static final SecureRandom RANDOM = new SecureRandom();

    private final RecuperacionPasswordRepository recuperacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RecuperacionPasswordEmitidaResponse emitir(Long usuarioId, Duration vigencia) {
        validarVigencia(vigencia);
        Usuario usuario = usuarioRepository.buscarPorIdConBloqueo(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el usuario", usuarioId));
        if (usuario.getEstado() != EstadoUsuario.ACTIVO || usuario.getPasswordHash() == null) {
            throw new ReglaNegocioException("Sólo se puede recuperar la contraseña de un usuario activo");
        }

        Instant ahora = Instant.now();
        var pendientes = recuperacionRepository
                .findAllByUsuarioIdAndUsadoEnIsNullAndRevocadoEnIsNull(usuarioId);
        pendientes.forEach(recuperacion -> recuperacion.setRevocadoEn(ahora));
        if (!pendientes.isEmpty()) {
            recuperacionRepository.saveAllAndFlush(pendientes);
        }

        String token = generarToken();
        RecuperacionPassword recuperacion = new RecuperacionPassword();
        recuperacion.setUsuario(usuario);
        recuperacion.setTokenHash(hash(token));
        recuperacion.setExpiraEn(ahora.plus(vigencia));
        recuperacionRepository.saveAndFlush(recuperacion);
        return new RecuperacionPasswordEmitidaResponse(token, recuperacion.getExpiraEn());
    }

    @Override
    public void restablecer(RestablecimientoPasswordRequest request) {
        validarPassword(request.password());
        RecuperacionPassword recuperacion = recuperacionRepository
                .buscarPorTokenHashConBloqueo(hash(request.token()))
                .orElseThrow(() -> new ReglaNegocioException("El enlace de recuperación no es válido"));
        Instant ahora = Instant.now();
        if (recuperacion.getUsadoEn() != null || recuperacion.getRevocadoEn() != null
                || !recuperacion.getExpiraEn().isAfter(ahora)) {
            throw new ReglaNegocioException("El enlace de recuperación venció o ya fue utilizado");
        }

        Usuario usuario = recuperacion.getUsuario();
        boolean bloqueoTemporal = usuario.getEstado() == EstadoUsuario.BLOQUEADO
                && usuario.getBloqueoHasta() != null;
        if (usuario.getEstado() != EstadoUsuario.ACTIVO && !bloqueoTemporal) {
            throw new ReglaNegocioException("La cuenta ya no está disponible para recuperación");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.password()));
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setBloqueoHasta(null);
        usuario.setIntentosFallidos(0);
        recuperacion.setUsadoEn(ahora);
        usuarioRepository.save(usuario);
        recuperacionRepository.saveAndFlush(recuperacion);
    }

    private void validarVigencia(Duration vigencia) {
        if (vigencia == null || vigencia.compareTo(VIGENCIA_MINIMA) < 0
                || vigencia.compareTo(VIGENCIA_MAXIMA) > 0) {
            throw new ReglaNegocioException("La recuperación debe vencer entre cinco minutos y dos horas");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.length() < 12
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException("La contraseña debe tener entre 12 y 72 caracteres");
        }
    }

    private String generarToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String token) {
        if (token == null || token.isBlank()) {
            throw new ReglaNegocioException("El enlace de recuperación no es válido");
        }
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 no está disponible", ex);
        }
    }
}
