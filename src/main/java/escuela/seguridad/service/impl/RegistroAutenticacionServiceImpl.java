package escuela.seguridad.service.impl;

import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.service.RegistroAutenticacionService;
import escuela.seguridad.service.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistroAutenticacionServiceImpl implements RegistroAutenticacionService {

    static final int MAXIMO_INTENTOS = 5;
    static final Duration DURACION_BLOQUEO = Duration.ofMinutes(15);

    private final UsuarioRepository usuarioRepository;
    private final SecurityProperties securityProperties;

    @Override
    public void registrarExito(UsuarioPrincipal principal) {
        if (principal == null || principal.accesoRecuperacion() || principal.usuarioId() == null) return;
        usuarioRepository.buscarPorIdConBloqueo(principal.usuarioId()).ifPresent(usuario -> {
            Instant ahora = Instant.now();
            if (usuario.getEstado() == EstadoUsuario.BLOQUEADO && bloqueoTemporalVencido(usuario, ahora)) {
                usuario.setEstado(EstadoUsuario.ACTIVO);
            }
            usuario.setUltimoAccesoEn(ahora);
            usuario.setIntentosFallidos(0);
            usuario.setBloqueoHasta(null);
            usuarioRepository.saveAndFlush(usuario);
        });
    }

    @Override
    public void registrarFallo(String identificador) {
        if (identificador == null || identificador.isBlank()
                || identificador.equals(securityProperties.getUser().getName())) return;
        localizar(identificador.trim()).map(Usuario::getId)
                .flatMap(usuarioRepository::buscarPorIdConBloqueo)
                .ifPresent(this::incrementarFallo);
    }

    private void incrementarFallo(Usuario usuario) {
        Instant ahora = Instant.now();
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            if (!bloqueoTemporalVencido(usuario, ahora)) return;
            usuario.setEstado(EstadoUsuario.ACTIVO);
            usuario.setIntentosFallidos(0);
            usuario.setBloqueoHasta(null);
        }
        if (usuario.getEstado() != EstadoUsuario.ACTIVO || usuario.getPasswordHash() == null) return;
        int intentos = usuario.getIntentosFallidos() + 1;
        usuario.setIntentosFallidos(intentos);
        if (intentos >= MAXIMO_INTENTOS) {
            usuario.setEstado(EstadoUsuario.BLOQUEADO);
            usuario.setBloqueoHasta(ahora.plus(DURACION_BLOQUEO));
        }
        usuarioRepository.saveAndFlush(usuario);
    }

    private Optional<Usuario> localizar(String identificador) {
        int separador = identificador.indexOf('\\');
        if (separador > 0 && separador < identificador.length() - 1) {
            String institucion = identificador.substring(0, separador).trim();
            String username = identificador.substring(separador + 1).trim();
            return usuarioRepository.findByInstitucionCodigoIgnoreCaseAndUsernameIgnoreCase(institucion, username);
        }
        List<Usuario> coincidencias = usuarioRepository.findAllByUsernameIgnoreCase(identificador);
        return coincidencias.size() == 1 ? Optional.of(coincidencias.getFirst()) : Optional.empty();
    }

    private boolean bloqueoTemporalVencido(Usuario usuario, Instant ahora) {
        return usuario.getBloqueoHasta() != null && !usuario.getBloqueoHasta().isAfter(ahora);
    }
}
