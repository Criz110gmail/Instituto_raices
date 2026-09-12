package escuela.seguridad.service.impl;

import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.repository.UsuarioRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.SecurityProperties;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioSistemaDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolPermisoRepository rolPermisoRepository;
    private final PermisoRepository permisoRepository;
    private final SecurityProperties securityProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String identificador) throws UsernameNotFoundException {
        if (securityProperties.getUser().getName().equals(identificador)) {
            return usuarioTemporal();
        }
        Usuario usuario = localizar(identificador);
        validarEstado(usuario);
        Set<String> permisos = permisos(usuario);
        return User.withUsername(usuario.getUsername())
                .password(usuario.getPasswordHash())
                .authorities(permisos.stream().map(SimpleGrantedAuthority::new).toList())
                .build();
    }

    private Usuario localizar(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            throw noEncontrado();
        }
        int separador = identificador.indexOf('\\');
        if (separador > 0 && separador < identificador.length() - 1) {
            String institucion = identificador.substring(0, separador).trim();
            String username = identificador.substring(separador + 1).trim();
            return usuarioRepository.findByInstitucionCodigoIgnoreCaseAndUsernameIgnoreCase(institucion, username)
                    .orElseThrow(this::noEncontrado);
        }
        List<Usuario> coincidencias = usuarioRepository.findAllByUsernameIgnoreCase(identificador.trim());
        if (coincidencias.size() != 1) {
            throw noEncontrado();
        }
        return coincidencias.getFirst();
    }

    private void validarEstado(Usuario usuario) {
        if (usuario.getEstado() == EstadoUsuario.BLOQUEADO) {
            throw new LockedException("La cuenta está bloqueada");
        }
        if (usuario.getEstado() != EstadoUsuario.ACTIVO || usuario.getPasswordHash() == null) {
            throw new DisabledException("La cuenta no está activa");
        }
    }

    private Set<String> permisos(Usuario usuario) {
        Set<String> codigos = new LinkedHashSet<>();
        usuarioRolRepository.findAllByUsuarioIdAndActivoTrueOrderByRolNombreAsc(usuario.getId()).stream()
                .filter(asignacion -> asignacion.getRol().isActivo())
                .forEach(asignacion -> rolPermisoRepository
                        .findAllByRolIdAndActivoTrueOrderByPermisoCodigoAsc(asignacion.getRol().getId())
                        .forEach(relacion -> codigos.add(relacion.getPermiso().getCodigo())));
        return codigos;
    }

    private UserDetails usuarioTemporal() {
        var configuracion = securityProperties.getUser();
        var autoridades = permisoRepository.findAllByOrderByCodigoAsc().stream()
                .map(permiso -> new SimpleGrantedAuthority(permiso.getCodigo()))
                .toList();
        return User.withUsername(configuracion.getName())
                .password(passwordEncoder.encode(configuracion.getPassword()))
                .authorities(autoridades)
                .build();
    }

    private UsernameNotFoundException noEncontrado() {
        return new UsernameNotFoundException("Usuario no encontrado");
    }
}
