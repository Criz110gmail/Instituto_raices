package escuela.seguridad.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public record UsuarioPrincipal(
        Long usuarioId,
        Long institucionId,
        Set<Long> plantelIds,
        boolean alcanceInstitucional,
        boolean accesoRecuperacion,
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities
) implements UserDetails {

    public UsuarioPrincipal {
        plantelIds = Set.copyOf(plantelIds);
        authorities = ListadoInmutable.copiar(authorities);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    private static final class ListadoInmutable {
        private static <T> java.util.List<T> copiar(Collection<? extends T> elementos) {
            return java.util.List.copyOf(elementos);
        }
    }
}
