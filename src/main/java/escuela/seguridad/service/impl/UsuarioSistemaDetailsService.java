package escuela.seguridad.service.impl;

import escuela.seguridad.entity.EstadoUsuario;
import escuela.seguridad.entity.Usuario;
import escuela.seguridad.repository.PermisoRepository;
import escuela.seguridad.repository.RolPermisoRepository;
import escuela.seguridad.repository.UsuarioRepository;
import escuela.seguridad.repository.UsuarioRolRepository;
import escuela.seguridad.service.UsuarioPrincipal;
import escuela.seguridad.service.ModuloPermiso;
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
import java.time.Instant;

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
        List<escuela.seguridad.entity.UsuarioRol> asignaciones = asignacionesActivas(usuario);
        Set<String> permisos = permisos(asignaciones);
        Set<Long> planteles = asignaciones.stream()
                .filter(a -> a.getAlcance() == escuela.seguridad.entity.AlcanceRol.PLANTEL)
                .map(a -> a.getPlantel().getId()).collect(java.util.stream.Collectors.toSet());
        boolean institucional = asignaciones.stream()
                .anyMatch(a -> a.getAlcance() == escuela.seguridad.entity.AlcanceRol.INSTITUCION);
        return new UsuarioPrincipal(usuario.getId(), usuario.getInstitucion().getId(), planteles,
                institucional, false, usuario.getUsername(), usuario.getPasswordHash(),
                permisos.stream().map(SimpleGrantedAuthority::new).toList());
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
            if (usuario.getBloqueoHasta() == null || usuario.getBloqueoHasta().isAfter(Instant.now())) {
                throw new LockedException("La cuenta está bloqueada");
            }
            return;
        }
        if (usuario.getEstado() != EstadoUsuario.ACTIVO || usuario.getPasswordHash() == null) {
            throw new DisabledException("La cuenta no está activa");
        }
    }

    private List<escuela.seguridad.entity.UsuarioRol> asignacionesActivas(Usuario usuario) {
        return usuarioRolRepository.findAllByUsuarioIdAndActivoTrueOrderByRolNombreAsc(usuario.getId()).stream()
                .filter(asignacion -> asignacion.getRol().isActivo())
                .toList();
    }

    private Set<String> permisos(List<escuela.seguridad.entity.UsuarioRol> asignaciones) {
        Set<String> codigos = new LinkedHashSet<>();
        asignaciones.forEach(asignacion -> rolPermisoRepository
                        .findAllByRolIdAndActivoTrueOrderByPermisoCodigoAsc(asignacion.getRol().getId())
                        .forEach(relacion -> codigos.add(relacion.getPermiso().getCodigo())));
        return ModuloPermiso.expandir(codigos);
    }

    private UserDetails usuarioTemporal() {
        var configuracion = securityProperties.getUser();
        var autoridades = permisoRepository.findAllByOrderByCodigoAsc().stream()
                .map(permiso -> new SimpleGrantedAuthority(permiso.getCodigo()))
                .toList();
        return new UsuarioPrincipal(null, null, Set.of(), true, true, configuracion.getName(),
                passwordEncoder.encode(configuracion.getPassword()), autoridades);
    }

    private UsernameNotFoundException noEncontrado() {
        return new UsernameNotFoundException("Usuario no encontrado");
    }
}
